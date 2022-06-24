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

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, DKMultiControlStreamManagerState) {
    DKMultiControlStreamManagerStateClosed = 0,
    DKMultiControlStreamManagerStateSlave,
    DKMultiControlStreamManagerStateMaster
};

@protocol DKMultiControlStreamManagerStateListener

@required
- (void)changeToState:(DKMultiControlStreamManagerState)state;

@end

/// @brief Main thread.
@interface DKMultiControlStreamManager : NSObject

@property(nonatomic, nullable, copy) NSString *(^searchIdConstructor)(NSURL *url);

@property(nonatomic, nullable, copy) void (^tcpHandler)(NSString *_Nullable message);

@property(readonly) DKMultiControlStreamManagerState state;

+ (instancetype)sharedInstance;

- (void)registerMultiControlStreamManagerStateListener:(id <DKMultiControlStreamManagerStateListener>)listener;

- (void)unregisterWithListener:(id <DKMultiControlStreamManagerStateListener>)listener;

- (void)enableMultiControlWithUrl:(NSURL *)url;

- (void)changeToMaster;

- (void)changeToSlave;

- (void)disableMultiControl;

- (nullable NSString *)recordWithUrlRequest:(NSURLRequest *)urlRequest;

- (void)recordWithHTTPUrlResponse:(NSHTTPURLResponse *)httpUrlResponse dataId:(NSString *)dataId responseBody:(nullable NSString *)responseBody;

- (void)queryWithUrlRequest:(NSURLRequest *)urlRequest completionBlock:(void (^)(NSError *_Nullable error, NSHTTPURLResponse *_Nullable response, NSData *_Nullable data))completionBlock;

- (void)broadcastWithActionMessage:(NSString *)message;

- (void)broadcastWithTCPMessage:(nullable NSString *)message;

@end

NS_ASSUME_NONNULL_END
