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

#import "DKMultiControlStreamManager.h"
#import <DoraemonKit/DKActionDTOModel.h>
#import <DoraemonKit/DKCommonDTOModel.h>
#import <DoraemonKit/DKWebSocketSession.h>
#import <DoraemonKit/DKDataRequestDTOModel.h>
#import <DoraemonKit/DKDataResponseDTOModel.h>
#import <DoraemonKit/DKMultiControlProtocol.h>
#if __has_include(<DoraemonKit/DoraemonMCCommandExcutor.h>)
#define HAS_MULTI_CONTROL 1
#import <DoraemonKit/DoraemonMCCommandExcutor.h>
#endif
NS_ASSUME_NONNULL_BEGIN

static NSString *generateId(void);

static NSString *const MULTI_CONTROL_HOST = @"mc_host";

//static NSString *const BEHAVIOR_ID = @"68753A444D6F12269C600050E4C00067";

@interface DKMultiControlStreamManager ()

@property(nonatomic, nullable, copy) NSString *behaviorId;

@property(nonatomic, nullable, strong) DKWebSocketSession *webSocketSession;

/// Default is NO.
@property(nonatomic, assign) BOOL isMaster;

@property(nonatomic, nullable, strong) NSHashTable<id <DKMultiControlStreamManagerStateListener>> *listenerArray;

@end

NS_ASSUME_NONNULL_END

NSString *generateId(void) {
    return [NSUUID.UUID.UUIDString stringByReplacingOccurrencesOfString:@"-" withString:@""];
}

@implementation DKMultiControlStreamManager

+ (instancetype)sharedInstance {
    static id _sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        _sharedInstance = [[self alloc] init];
    });

    return _sharedInstance;
}

- (void)registerMultiControlStreamManagerStateListener:(id <DKMultiControlStreamManagerStateListener>)listener {
    if (!self.listenerArray) {
        self.listenerArray = NSHashTable.weakObjectsHashTable;
    }
    [self.listenerArray addObject:listener];
    [listener changeToState:self.webSocketSession ? (self.isMaster ? DKMultiControlStreamManagerStateMaster : DKMultiControlStreamManagerStateSlave) : DKMultiControlStreamManagerStateClosed];
}

- (void)unregisterWithListener:(id)listener {
    [self.listenerArray removeObject:listener];
    if (!self.listenerArray.count) {
        self.listenerArray = nil;
    }
}

- (void)enableMultiControlWithUrl:(NSURL *)url {
    if (self.webSocketSession) {
        return;
    }
    self.webSocketSession = [[DKWebSocketSession alloc] initWithUrl:url];
    __weak typeof(self) weakSelf = self;
    self.webSocketSession.notifyHandler = ^(DKCommonDTOModel *commonDTOModel) {
        typeof(weakSelf) self = weakSelf;
        if ([commonDTOModel.dataType isEqualToString:MULTI_CONTROL_HOST]) {
            [self changeToSlave];
        } else if ([commonDTOModel.dataType isEqualToString:DK_ACTION]) {
            // Handle behaviorId and process data.
            NSData *jsonData = [commonDTOModel.data dataUsingEncoding:NSUTF8StringEncoding];
            if (jsonData) {
                NSError *error = nil;
                NSDictionary *jsonDictionary = [NSJSONSerialization JSONObjectWithData:jsonData options:0 error:&error];
                if (jsonDictionary) {
                    DKActionDTOModel *actionDTOModel = [MTLJSONAdapter modelOfClass:DKActionDTOModel.class fromJSONDictionary:jsonDictionary error:&error];
                    self.behaviorId = actionDTOModel.behaviorId;
                    if (actionDTOModel.payload) {
#if HAS_ENCRYPT_APOLLO
                        [DoraemonMCCommandExcutor excuteMessageStrFromNet:actionDTOModel.payload];
#endif
                    }
                }
            }
        } else if ([commonDTOModel.dataType isEqualToString:DK_TCP]) {
            self.tcpHandler ? self.tcpHandler(commonDTOModel.data) : nil;
        }
    };
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateSlave];
    }
    [NSURLProtocol registerClass:DKMultiControlProtocol.class];
}

- (void)changeToMaster {
    if (!self.webSocketSession) {
        for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
            [listener changeToState:DKMultiControlStreamManagerStateClosed];
        }

        return;
    }
    if (self.isMaster) {
//        for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
//            [listener changeToState:DKMultiControlStreamManagerStateMaster];
//        }

        return;
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.dataType = MULTI_CONTROL_HOST;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.method = DK_WEBSOCKET_BROADCAST;
    commonDTOModel.requestId = nil;
    commonDTOModel.data = nil;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *jsonString = nil;
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (!jsonString) {
        return;
    }
    [self.webSocketSession sendString:jsonString requestId:nil completionHandler:nil];
    self.isMaster = YES;
    self.behaviorId = generateId();
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateMaster];
    }
}

- (void)changeToSlave {
    if (!self.webSocketSession || !self.isMaster) {
        return;
    }
    self.isMaster = NO;
    self.behaviorId = nil;
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateSlave];
    }
}

- (DKMultiControlStreamManagerState)state {
    if (!self.webSocketSession) {
        return DKMultiControlStreamManagerStateClosed;
    }

    return self.isMaster ? DKMultiControlStreamManagerStateMaster : DKMultiControlStreamManagerStateSlave;
}

- (void)disableMultiControl {
    if (!self.webSocketSession) {
        return;
    }
    self.isMaster = NO;
    self.webSocketSession = nil;
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateClosed];
    }
    [NSURLProtocol unregisterClass:DKMultiControlProtocol.class];
}

- (NSString *)recordWithUrlRequest:(NSURLRequest *)urlRequest {
    if (!self.webSocketSession || !urlRequest.URL || urlRequest.HTTPBodyStream.delegate) {
        return nil;
    }
    DKDataRequestDTOModel *dataRequestDTOModel = [[DKDataRequestDTOModel alloc] init];
    dataRequestDTOModel.behaviorId = self.behaviorId;
    dataRequestDTOModel.dataId = generateId();
    dataRequestDTOModel.method = urlRequest.HTTPMethod;
    dataRequestDTOModel.url = urlRequest.URL;
    NSURLComponents *urlComponents = [NSURLComponents componentsWithURL:urlRequest.URL resolvingAgainstBaseURL:YES];
    urlComponents.fragment = nil;
    urlComponents.password = nil;
    urlComponents.query = nil;
    urlComponents.user = nil;
    dataRequestDTOModel.searchId = urlComponents.string;
    if (self.searchIdConstructor) {
        NSString *searchId = self.searchIdConstructor(urlRequest.URL);
        if (searchId.length > 0) {
            dataRequestDTOModel.searchId = searchId;
        }
    }
    dataRequestDTOModel.requestHeader = urlRequest.allHTTPHeaderFields;
    if (urlRequest.HTTPBodyStream) {
        NSInputStream *inputStream = urlRequest.HTTPBodyStream;
        [inputStream open];
        uint8_t buffer[10] = {0};
        NSMutableData *data = nil;
        while (inputStream.hasBytesAvailable) {
            NSInteger length = [inputStream read:buffer maxLength:10];
            if (length > 0) {
                if (!data) {
                    data = [NSMutableData dataWithBytes:buffer length:length];
                } else {
                    [data appendBytes:buffer length:length];
                }
            }
        }
        [inputStream close];
        if (data) {
            dataRequestDTOModel.requestBody = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        }
    }
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:dataRequestDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = nil;
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = dataString;
    commonDTOModel.method = DK_METHOD_DATA;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.dataType = DK_DATA_REQUEST;
    jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (!dataString) {
        return nil;
    }
    [self.webSocketSession sendString:dataString requestId:nil completionHandler:nil];

    return dataRequestDTOModel.dataId;
}

- (void)recordWithHTTPUrlResponse:(NSHTTPURLResponse *)httpUrlResponse dataId:(NSString *)dataId responseBody:(nullable NSString *)responseBody {
    if (!self.webSocketSession) {
        return;
    }
    DKDataResponseDTOModel *dataResponseDTOModel = [[DKDataResponseDTOModel alloc] init];
    dataResponseDTOModel.dataId = dataId;
    dataResponseDTOModel.responseCode = httpUrlResponse.statusCode;
    dataResponseDTOModel.responseHeader = httpUrlResponse.allHeaderFields;
    dataResponseDTOModel.responseBody = responseBody;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:dataResponseDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = nil;
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = dataString;
    commonDTOModel.method = DK_METHOD_DATA;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.dataType = DK_DATA_RESPONSE;
    jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (dataString) {
        [self.webSocketSession sendString:dataString requestId:nil completionHandler:nil];
    }
}

- (void)queryWithUrlRequest:(NSURLRequest *)urlRequest completionBlock:(void (^)(NSError *, NSHTTPURLResponse *, NSData *))completionBlock {
    if (!self.webSocketSession || !urlRequest.URL || !self.behaviorId) {
        completionBlock(nil, nil, nil);

        return;
    }
    DKDataRequestDTOModel *dataRequestDTOModel = [[DKDataRequestDTOModel alloc] init];
    dataRequestDTOModel.behaviorId = self.behaviorId;
    dataRequestDTOModel.dataId = nil;
    dataRequestDTOModel.method = urlRequest.HTTPMethod;
    dataRequestDTOModel.url = urlRequest.URL;
    NSURLComponents *urlComponents = [NSURLComponents componentsWithURL:urlRequest.URL resolvingAgainstBaseURL:YES];
    urlComponents.fragment = nil;
    urlComponents.password = nil;
    urlComponents.query = nil;
    urlComponents.user = nil;
    dataRequestDTOModel.searchId = urlComponents.string;
    if (self.searchIdConstructor) {
        NSString *searchId = self.searchIdConstructor(urlRequest.URL);
        if (searchId.length > 0) {
            dataRequestDTOModel.searchId = searchId;
        }
    }
    dataRequestDTOModel.requestHeader = urlRequest.allHTTPHeaderFields;
    dataRequestDTOModel.requestBody = nil;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:dataRequestDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = @(self.webSocketSession.requestId++);
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = dataString;
    commonDTOModel.method = DK_METHOD_DATA;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.dataType = DK_DATA_QUERY;
    jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (!dataString) {
        completionBlock(nil, nil, nil);

        return;
    }
    [self.webSocketSession sendString:dataString requestId:commonDTOModel.requestId completionHandler:^(NSError *_Nullable error, NSString *_Nullable responseString) {
        if (error) {
            completionBlock(error, nil, nil);

            return;
        }
        NSData *data = [responseString dataUsingEncoding:NSUTF8StringEncoding];
        if (!responseString) {
            completionBlock(nil, nil, nil);

            return;
        }
        NSDictionary *jsonDictionary = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error];
        if (error) {
            completionBlock(error, nil, nil);

            return;
        }
        DKDataResponseDTOModel *dataResponseDTOModel = [MTLJSONAdapter modelOfClass:DKDataResponseDTOModel.class fromJSONDictionary:jsonDictionary ?: @{} error:&error];
        if (error) {
            completionBlock(error, nil, nil);

            return;
        }
        NSHTTPURLResponse *httpUrlResponse = [[NSHTTPURLResponse alloc] initWithURL:urlRequest.URL statusCode:dataResponseDTOModel.responseCode ?: 404 HTTPVersion:@"HTTP/1.1" headerFields:dataResponseDTOModel.responseHeader];
        completionBlock(nil, httpUrlResponse, [dataResponseDTOModel.responseBody dataUsingEncoding:NSUTF8StringEncoding]);
    }];
}

- (void)broadcastWithActionMessage:(NSString *)message {
    if (!self.webSocketSession || !self.behaviorId) {
        return;
    }
    DKActionDTOModel *actionDTOModel = [[DKActionDTOModel alloc] init];
    actionDTOModel.payload = message;
    self.behaviorId = generateId();
    actionDTOModel.behaviorId = self.behaviorId;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:actionDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = nil;
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = dataString;
    commonDTOModel.method = DK_WEBSOCKET_BROADCAST;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.dataType = DK_ACTION;
    jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (!dataString) {
        return;
    }
    [self.webSocketSession sendString:dataString requestId:nil completionHandler:nil];
}

- (void)broadcastWithTCPMessage:(NSString *)message {
    if (!self.webSocketSession) {
        return;
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.requestId = nil;
    commonDTOModel.deviceType = DK_DEVICE_TYPE;
    commonDTOModel.data = message;
    commonDTOModel.method = DK_WEBSOCKET_BROADCAST;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.dataType = DK_ACTION;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *dataString = nil;
    if (jsonData) {
        dataString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    if (!dataString) {
        return;
    }
    [self.webSocketSession sendString:dataString requestId:nil completionHandler:nil];
}

@end
