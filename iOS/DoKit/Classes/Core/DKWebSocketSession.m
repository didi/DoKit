/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "DKWebSocketSession.h"
#import <DoraemonKit/DKCommonDTOModel.h>
#import <DoraemonKit/DKLoginDataDTOModel.h>
#import <SocketRocket/SRWebSocket.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DKWebSocketRequestBlock)(NSError *_Nullable error, DKWebSocketSession *_Nullable webSocketSession);

static NSString *const DOKIT_WEBSOCKET_SESSION = @"DOKIT_WEBSOCKET_SESSION";

static NSString *const DEVICE_AUTHENTICATION_ERROR = @"Device authentication json error.";

static NSString *const NSJSONSERIALIZATION_ERROR = @"Dictionary to json error.";

@interface DKWebSocketSession () <SRWebSocketDelegate>

@property(nonatomic, copy) NSURL *url;

@property(nonatomic, nullable, weak) SRWebSocket *webSocket;

@property(nonatomic, assign) int requestId;

@property(nonatomic, nullable, copy) NSUUID *sessionUUID;

@property(nonatomic, nullable, copy) NSArray<DKWebSocketRequestBlock> *deferRequestQueue;

@property(nonatomic, nullable, copy) DKWebSocketRequestBlock deviceAuthenticationRequestBlock;

@property(nonatomic, nullable, copy) NSDictionary<NSString *, DKWebSocketCompletionHandler> *completionHandlerDictionary;

- (void)connect;

- (void)deviceAuthentication;

@end

NS_ASSUME_NONNULL_END

@implementation DKWebSocketSession

- (instancetype)initWithUrl:(NSURL *)url {
    self = [super init];
    _url = url.copy;
    _requestId = 0;
    id webSocketSession = [NSUserDefaults.standardUserDefaults objectForKey:DOKIT_WEBSOCKET_SESSION];
    if ([webSocketSession isKindOfClass:NSString.class]) {
        _sessionUUID = [[NSUUID alloc] initWithUUIDString:webSocketSession];
    }
    [self deviceAuthentication];

    return self;
}

- (void)connect {
    if (_webSocket && (_webSocket.readyState == SR_CONNECTING || _webSocket.readyState == SR_OPEN)) {
        return;
    }
    SRWebSocket *webSocket = [[SRWebSocket alloc] initWithURL:_url];
    _webSocket = webSocket;
    webSocket.delegate = self;
    [webSocket open];
}

- (void)deviceAuthentication {
    if (_webSocket.readyState != SR_OPEN) {
        [self connect];
    }
    if (!_sessionUUID) {
        DKWebSocketRequestBlock webSocketRequestBlock = ^(NSError *_Nullable error, DKWebSocketSession *_Nullable webSocketSession) {
            if (error || !webSocketSession) {
                return;
            }
            DKLoginDataDTOModel *loginDataDTOModel = [[DKLoginDataDTOModel alloc] init];
            loginDataDTOModel.manufacturer = nil;
            loginDataDTOModel.connectSerial = webSocketSession.sessionUUID;
            NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:loginDataDTOModel error:&error];
            NSAssert(!error, DEVICE_AUTHENTICATION_ERROR);
            NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
            NSAssert(!error, NSJSONSERIALIZATION_ERROR);
            NSString *dataString = nil;
            if (jsonData) {
                dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            }
            DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
            commonDTOModel.requestId = @(webSocketSession.requestId++).stringValue;
            commonDTOModel.data = dataString;
            commonDTOModel.method = @"LOGIN";
            jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
            NSAssert(!error, DEVICE_AUTHENTICATION_ERROR);
            jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
            NSAssert(!error, NSJSONSERIALIZATION_ERROR);
            dataString = nil;
            if (jsonData) {
                dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            }
            [webSocketSession.webSocket sendString:dataString error:nil];
            // Add completionHandler to responseQueue.
            __weak typeof(webSocketSession) weakWebSocketSession = webSocketSession;
            DKWebSocketCompletionHandler webSocketCompletionHandler = ^(NSError *_Nullable error, NSString *_Nullable dataString) {
                id jsonObject = [NSJSONSerialization JSONObjectWithData:[(dataString ?: @"") dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
                if (![jsonObject isKindOfClass:NSDictionary.class]) {
                    return;
                }
                DKLoginDataDTOModel *loginDataDTOModel = [MTLJSONAdapter modelOfClass:DKLoginDataDTOModel.class fromJSONDictionary:jsonObject error:&error];
                typeof(weakWebSocketSession) self = weakWebSocketSession;
                self.sessionUUID = loginDataDTOModel.connectSerial;
            };
            NSMutableDictionary<NSString *, DKWebSocketCompletionHandler> *completionHandlerDictionary = webSocketSession.completionHandlerDictionary.mutableCopy;
            webSocketSession.completionHandlerDictionary = nil;
            if (!completionHandlerDictionary) {
                completionHandlerDictionary = NSMutableDictionary.dictionary;
            }
            completionHandlerDictionary[commonDTOModel.requestId] = webSocketCompletionHandler;
            webSocketSession.completionHandlerDictionary = completionHandlerDictionary;
        };
        if (_webSocket.readyState == SR_OPEN) {
            webSocketRequestBlock(nil, self);
        } else {
            // SR_CONNECTING
            // Add request to deferRequestQueue.
            self.deviceAuthenticationRequestBlock = webSocketRequestBlock;
//            NSMutableArray<DKWebSocketRequestBlock> *deferRequestQueue = _deferRequestQueue.mutableCopy;
//            _deferRequestQueue = nil;
//            if (!deferRequestQueue) {
//                deferRequestQueue = NSMutableArray.array;
//            }
//            [deferRequestQueue addObject:webSocketRequestBlock];
//            _deferRequestQueue = deferRequestQueue.copy;
        }
    }
}

- (void)dealloc {
    [NSUserDefaults.standardUserDefaults setObject:self.sessionUUID.UUIDString forKey:DOKIT_WEBSOCKET_SESSION];
    [NSUserDefaults.standardUserDefaults synchronize];
    [self.webSocket close];
}

#pragma mark - SRWebSocketDelegate

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessageWithString:(NSString *)string {
    NSError *error = nil;
    id jsonObject = [NSJSONSerialization JSONObjectWithData:[(string ?: @"") dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
    if (![jsonObject isKindOfClass:NSDictionary.class]) {
        return;
    }
    DKCommonDTOModel *commonDTOModel = [MTLJSONAdapter modelOfClass:DKCommonDTOModel.class fromJSONDictionary:jsonObject error:&error];
    if (commonDTOModel.requestId) {
        DKWebSocketCompletionHandler webSocketCompletionHandler = self.completionHandlerDictionary[commonDTOModel.requestId];
        if (webSocketCompletionHandler) {
            NSMutableDictionary<NSString *, DKWebSocketCompletionHandler> *completionHandlerDictionary = self.completionHandlerDictionary.mutableCopy;
            self.completionHandlerDictionary = nil;
            completionHandlerDictionary[commonDTOModel.requestId] = nil;
            self.completionHandlerDictionary = completionHandlerDictionary;
            webSocketCompletionHandler(nil, commonDTOModel.data);
        }
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    if (self.deviceAuthenticationRequestBlock) {
        self.deviceAuthenticationRequestBlock(error, nil);
        self.deviceAuthenticationRequestBlock = nil;
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    if (self.deviceAuthenticationRequestBlock) {
        self.deviceAuthenticationRequestBlock(nil, nil);
        self.deviceAuthenticationRequestBlock = nil;
    }
}

- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    if (!self.sessionUUID) {
        self.deviceAuthenticationRequestBlock(nil, self);
        self.deviceAuthenticationRequestBlock = nil;
    }
}

@end
