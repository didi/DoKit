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

NSString *DK_WEBSOCKET_BROADCAST = @"BROADCAST";

typedef void (^DKWebSocketRequestBlock)(NSError *_Nullable error, DKWebSocketSession *_Nullable webSocketSession);

static NSString *const DOKIT_WEBSOCKET_SESSION = @"DOKIT_WEBSOCKET_SESSION";

static NSString *const DEVICE_AUTHENTICATION_ERROR = @"Device authentication json error.";

static NSString *const JSON_SERIALIZATION_ERROR = @"Dictionary to json error.";

@interface DKWebSocketSession () <SRWebSocketDelegate>

/// Is Open/Connecting state.
@property(readonly) BOOL isWebSocketRunning;

/// Current is DeviceAuthentication state.
@property(nonatomic, assign) BOOL isDeviceAuthenticating;

@property(nonatomic, copy) NSURL *url;

@property(nonatomic, nullable, weak) SRWebSocket *webSocket;

@property(nonatomic, nullable, copy) NSUUID *sessionUUID;

@property(nonatomic, nullable, copy) NSArray<DKWebSocketRequestBlock> *deferRequestQueue;

@property(nonatomic, nullable, copy) NSDictionary<NSNumber *, DKWebSocketCompletionHandler> *completionHandlerDictionary;

/// Make WebSocket connection available.
- (void)connect;

- (void)deviceAuthentication;

- (void)handleWebSocketWithError:(nullable NSError *)error;

- (void)addWithRequestId:(unsigned int)requestId webSocketCompletionHandler:(DKWebSocketCompletionHandler)webSocketCompletionHandler;

- (void)handleWithDeviceAuthenticationError:(nullable NSError *)deviceAuthenticationError;

- (void)sendDeviceAuthenticationRequest;

@end

NS_ASSUME_NONNULL_END

@implementation DKWebSocketSession

- (void)sendDeviceAuthenticationRequest {
    NSAssert(self.isDeviceAuthenticating, @"State error.");
    DKLoginDataDTOModel *loginDataDTOModel = [[DKLoginDataDTOModel alloc] init];
    loginDataDTOModel.manufacturer = @"Apple";
    // Load previous UUID.
    loginDataDTOModel.connectSerial = self.sessionUUID;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:loginDataDTOModel error:&error];

    NSAssert(!error, DEVICE_AUTHENTICATION_ERROR);
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];

    NSAssert(!error, JSON_SERIALIZATION_ERROR);
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = @(self.requestId++);
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = dataString;
    commonDTOModel.method = DK_METHOD_LOGIN;
    commonDTOModel.connectSerial = self.sessionUUID;
    commonDTOModel.dataType = nil;
    jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];

    NSAssert(!error, DEVICE_AUTHENTICATION_ERROR);
    jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];

    NSAssert(!error, JSON_SERIALIZATION_ERROR);
    dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    [self.webSocket sendString:dataString error:nil];
    // Add completionHandler to responseQueue.
    __weak typeof(self) weakSelf = self;
    DKWebSocketCompletionHandler webSocketCompletionHandler = ^(NSError *_Nullable error, NSString *_Nullable dataString) {
        id jsonObject = [NSJSONSerialization JSONObjectWithData:[(dataString ?: @"") dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
        typeof(weakSelf) self = weakSelf;
        if (![jsonObject isKindOfClass:NSDictionary.class]) {
            [self handleWithDeviceAuthenticationError:error];

            return;
        }
        DKLoginDataDTOModel *loginDataDTOModel = [MTLJSONAdapter modelOfClass:DKLoginDataDTOModel.class fromJSONDictionary:jsonObject error:&error];
        self.sessionUUID = loginDataDTOModel.connectSerial;

        self.isDeviceAuthenticating = NO;
        // Trigger deferred request.
        [self.deferRequestQueue enumerateObjectsUsingBlock:^(DKWebSocketRequestBlock obj, NSUInteger __attribute__((unused)) idx, BOOL *__attribute__((unused)) stop) {
            obj(nil, self);
        }];
        self.deferRequestQueue = nil;
    };
    [self addWithRequestId:commonDTOModel.requestId.unsignedIntValue webSocketCompletionHandler:webSocketCompletionHandler];
}

- (void)addWithRequestId:(unsigned int)requestId webSocketCompletionHandler:(DKWebSocketCompletionHandler)webSocketCompletionHandler {
    NSMutableDictionary<NSNumber *, DKWebSocketCompletionHandler> *completionHandlerDictionary = self.completionHandlerDictionary.mutableCopy;
    self.completionHandlerDictionary = nil;
    if (!completionHandlerDictionary) {
        completionHandlerDictionary = NSMutableDictionary.dictionary;
    }
    completionHandlerDictionary[@(requestId)] = webSocketCompletionHandler;
    self.completionHandlerDictionary = completionHandlerDictionary;
}

- (BOOL)isWebSocketRunning {
    return _webSocket && (_webSocket.readyState == SR_CONNECTING || _webSocket.readyState == SR_OPEN);
}

- (void)handleWithDeviceAuthenticationError:(nullable NSError *)deviceAuthenticationError {
    // Enumerate all request to tell that an error is happened.
    [self.deferRequestQueue enumerateObjectsUsingBlock:^(DKWebSocketRequestBlock obj, NSUInteger __attribute__((unused)) idx, BOOL *__attribute__((unused)) stop) {
        obj(deviceAuthenticationError, self);
    }];
    self.deferRequestQueue = nil;
    self.isDeviceAuthenticating = NO;
}

- (instancetype)initWithUrl:(NSURL *)url {
    self = [super init];
    _url = url.copy;
    _isDeviceAuthenticating = NO;
    _requestId = 0;
    id webSocketSession = [NSUserDefaults.standardUserDefaults objectForKey:DOKIT_WEBSOCKET_SESSION];
    if ([webSocketSession isKindOfClass:NSString.class]) {
        _sessionUUID = [[NSUUID alloc] initWithUUIDString:webSocketSession];
    }
    [self deviceAuthentication];

    return self;
}

- (void)connect {
    if (self.isWebSocketRunning) {
        return;
    }
    SRWebSocket *webSocket = [[SRWebSocket alloc] initWithURL:_url];
    _webSocket = webSocket;
    webSocket.delegate = self;
    [webSocket open];
}

- (void)deviceAuthentication {
    [self connect];
    if (_isDeviceAuthenticating) {
        return;
    }
    _isDeviceAuthenticating = YES;
    if (_webSocket.readyState == SR_OPEN) {
        // SR_CONNECTING, defer request.
        [self sendDeviceAuthenticationRequest];
    }
}

- (void)sendString:(NSString *)string requestId:(NSNumber *)requestId completionHandler:(nullable DKWebSocketCompletionHandler)completionHandler {
    if (!self.isWebSocketRunning || self.isDeviceAuthenticating) {
        if (!self.isWebSocketRunning) {
            [self deviceAuthentication];
        }
        // Add to deferRequestQueue.
        DKWebSocketRequestBlock webSocketRequestBlock = ^(NSError *_Nullable error, DKWebSocketSession *_Nullable webSocketSession) {
            if (error || !webSocketSession) {
                completionHandler ? completionHandler(error, nil) : (void) nil;

                return;
            }
            [webSocketSession.webSocket sendString:string error:nil];
            if (requestId && completionHandler) {
                [webSocketSession addWithRequestId:requestId.unsignedIntValue webSocketCompletionHandler:completionHandler];
            }
        };
        NSMutableArray<DKWebSocketRequestBlock> *deferRequestQueue = self.deferRequestQueue.mutableCopy;
        self.deferRequestQueue = nil;
        if (!deferRequestQueue) {
            deferRequestQueue = NSMutableArray.array;
        }
        [deferRequestQueue addObject:webSocketRequestBlock];
        self.deferRequestQueue = deferRequestQueue;
    } else {
        // Send request.
        [self.webSocket sendString:string error:nil];
        if (requestId && completionHandler) {
            [self addWithRequestId:requestId.unsignedIntValue webSocketCompletionHandler:completionHandler];
        }
    }
}

- (void)setSessionUUID:(NSUUID *)sessionUUID {
    _sessionUUID = sessionUUID.copy;
    [NSUserDefaults.standardUserDefaults setObject:sessionUUID.UUIDString forKey:DOKIT_WEBSOCKET_SESSION];
}

- (void)dealloc {
    [_webSocket close];
}

- (void)handleWebSocketWithError:(nullable NSError *)error {
    if (self.isDeviceAuthenticating) {
        [self handleWithDeviceAuthenticationError:error];
        self.completionHandlerDictionary = nil;
    } else {
        [self.completionHandlerDictionary enumerateKeysAndObjectsUsingBlock:^(NSNumber *__attribute__((unused)) key, DKWebSocketCompletionHandler obj, BOOL *__attribute__((unused)) stop) {
            obj(error, nil);
        }];
        self.completionHandlerDictionary = nil;
    }
}

#pragma mark - SRWebSocketDelegate

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessageWithString:(NSString *)string {
    NSError *error = nil;
    id jsonObject = [NSJSONSerialization JSONObjectWithData:[(string ?: @"") dataUsingEncoding:NSUTF8StringEncoding] options:0 error:&error];
    if (![jsonObject isKindOfClass:NSDictionary.class]) {
        return;
    }
    DKCommonDTOModel *commonDTOModel = [MTLJSONAdapter modelOfClass:DKCommonDTOModel.class fromJSONDictionary:jsonObject error:&error];
    if ([commonDTOModel.method isEqualToString:DK_WEBSOCKET_BROADCAST]) {
        self.notifyHandler ? self.notifyHandler(commonDTOModel) : (void) nil;
    } else if (commonDTOModel.requestId) {
        DKWebSocketCompletionHandler webSocketCompletionHandler = self.completionHandlerDictionary[commonDTOModel.requestId];
        if (webSocketCompletionHandler) {
            NSMutableDictionary<NSNumber *, DKWebSocketCompletionHandler> *completionHandlerDictionary = self.completionHandlerDictionary.mutableCopy;
            self.completionHandlerDictionary = nil;
            completionHandlerDictionary[commonDTOModel.requestId] = nil;
            if (completionHandlerDictionary.count == 0) {
                completionHandlerDictionary = nil;
            }
            self.completionHandlerDictionary = completionHandlerDictionary;
            webSocketCompletionHandler(nil, commonDTOModel.data);
        }
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    [self handleWebSocketWithError:error];
}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    [self handleWebSocketWithError:nil];
}

- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    if (self.isDeviceAuthenticating) {
        [self sendDeviceAuthenticationRequest];
    }
}

@end
