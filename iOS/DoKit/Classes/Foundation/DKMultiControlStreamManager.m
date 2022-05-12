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
#import <DoraemonKit/DKCommonDTOModel.h>
#import <DoraemonKit/DKWebSocketSession.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const MULTI_CONTROL_HOST = @"mc_host";

@interface DKMultiControlStreamManager ()

@property(nonatomic, nullable, strong) DKWebSocketSession *webSocketSession;

/// Default is NO.
@property(nonatomic, assign) BOOL isMaster;

@property(nonatomic, nullable, strong) NSHashTable<id <DKMultiControlStreamManagerStateListener>> *listenerArray;

@end

NS_ASSUME_NONNULL_END

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
        }
    };
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateSlave];
    }
}

- (void)changeToMaster {
    if (!self.webSocketSession || self.isMaster) {
        return;
    }
    DKCommonDTOModel *commonDTOModel = [[DKCommonDTOModel alloc] init];
    commonDTOModel.dataType = MULTI_CONTROL_HOST;
    commonDTOModel.connectSerial = self.webSocketSession.sessionUUID;
    commonDTOModel.method = DK_WEBSOCKET_BROADCAST;
    NSError *error = nil;
    NSDictionary *jsonDictionary = [MTLJSONAdapter JSONDictionaryFromModel:commonDTOModel error:&error];
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary ?: @{} options:0 error:&error];
    NSString *jsonString = nil;
    if (jsonData) {
        jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    }
    [self.webSocketSession sendString:jsonString requestId:nil completionHandler:nil];
    self.isMaster = YES;
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateMaster];
    }
}

- (void)changeToSlave {
    if (!self.webSocketSession || !self.isMaster) {
        return;
    }
    self.isMaster = NO;
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
    self.webSocketSession = nil;
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateClosed];
    }
}

@end
