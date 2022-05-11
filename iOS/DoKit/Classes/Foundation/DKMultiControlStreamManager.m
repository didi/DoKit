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

NS_ASSUME_NONNULL_BEGIN

@interface DKMultiControlStreamManager ()

@property (nonatomic, nullable, )

@property(nonatomic, nullable, copy) NSArray<DKMultiControlSteamManagerListener> *listenerArray;

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

- (void)registerMultiControlStreamManagerStateListener:(DKMultiControlSteamManagerListener)listener {
    NSMutableArray<DKMultiControlSteamManagerListener> *listenerArray = self.listenerArray.mutableCopy;
    self.listenerArray = nil;
    if (!listenerArray) {
        listenerArray = NSMutableArray.array;
    }
    [listenerArray addObject:listener];
    self.listenerArray = listenerArray;
}

- (void)enableMultiControl {

}

@end
