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

extern NSString *DK_DEVICE_TYPE;

extern NSString *DK_METHOD_LOGIN;

extern NSString *DK_METHOD_DATA;

extern NSString *DK_DATA_REQUEST;

extern NSString *DK_DATA_RESPONSE;

extern NSString *DK_DATA_QUERY;

extern NSString *DK_ACTION;

extern NSString *DK_TCP;

@interface DKCommonDTOModel : MTLModel <MTLJSONSerializing>

@property(nonatomic, nullable, copy) NSNumber *requestId;

/// Main type.
@property(nonatomic, nullable, copy) NSString *method;

@property(nonatomic, nullable, copy) NSString *data;

@property(nonatomic, nullable, copy) NSUUID *connectSerial;

@property(nonatomic, nullable, copy) NSString *deviceType;

// Subtype.
@property(nonatomic, nullable, copy) NSString *dataType;

@end

NS_ASSUME_NONNULL_END
