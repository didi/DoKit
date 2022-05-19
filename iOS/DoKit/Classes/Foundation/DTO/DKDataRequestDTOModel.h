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

#import <Mantle/Mantle.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKDataRequestDTOModel : MTLModel <MTLJSONSerializing>

@property(nonatomic, nullable, copy) NSString *behaviorId;

/// query will set dataId to nil.
@property(nonatomic, nullable, copy) NSString *dataId;

@property(nonatomic, nullable, copy) NSString *searchId;

@property(nonatomic, nullable, copy) NSString *method;

@property(nonatomic, nullable, copy) NSURL *url;

@property(nonatomic, nullable, copy) NSDictionary<NSString *, NSString *> *requestHeader;

@property(nonatomic, nullable, copy) NSString *requestBody;

@end

NS_ASSUME_NONNULL_END
