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

@class DKCommonDTOModel;

NS_ASSUME_NONNULL_BEGIN

extern NSString *DK_WEBSOCKET_BROADCAST;

typedef void (^DKWebSocketCompletionHandler)(NSError *_Nullable error, NSString *_Nullable responseString);

@interface DKWebSocketSession : NSObject

@property(nullable, readonly, nonatomic, copy) NSUUID *sessionUUID;

@property(nonatomic, assign) unsigned int requestId;

@property(nonatomic, nullable, copy) void (^notifyHandler)(DKCommonDTOModel *commonDTOModel);

- (instancetype)init NS_UNAVAILABLE;

- (instancetype)initWithUrl:(NSURL *)url NS_DESIGNATED_INITIALIZER;

- (void)sendString:(NSString *)string requestId:(nullable NSNumber *)requestId completionHandler:(nullable DKWebSocketCompletionHandler)completionHandler;

@end

NS_ASSUME_NONNULL_END
