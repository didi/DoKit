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
#import <DoraemonKit/DKWebSocketSession.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKMultiControlStreamManager ()

@property(nonatomic, nullable, strong) DKWebSocketSession *webSocketSession;

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
    [listener changeToState:self.webSocketSession ? DKMultiControlStreamManagerStateRunning : DKMultiControlStreamManagerStateClosed];
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
    for (id <DKMultiControlStreamManagerStateListener> listener in self.listenerArray) {
        [listener changeToState:DKMultiControlStreamManagerStateRunning];
    }
}

- (BOOL)isEnabled {
    return (BOOL) self.webSocketSession;
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
