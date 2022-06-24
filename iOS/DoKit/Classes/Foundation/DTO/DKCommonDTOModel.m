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

#import "DKCommonDTOModel.h"

NSString *DK_DEVICE_TYPE = @"ios";

NSString *DK_METHOD_LOGIN = @"LOGIN";

NSString *DK_METHOD_DATA = @"DATA";

NSString *DK_DATA_REQUEST = @"request";

NSString *DK_DATA_RESPONSE = @"response";

NSString *DK_DATA_QUERY = @"query";

NSString *DK_ACTION = @"action";

NSString *DK_TCP = @"tcp";

@implementation DKCommonDTOModel

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
            @"requestId": @"pid",
            @"method": @"type",
            @"data": @"data",
            @"connectSerial": @"connectSerial",
            @"dataType": @"contentType",
            @"deviceType": @"channelSerial"
    };
}

+ (NSValueTransformer *)connectSerialJSONTransformer {
    return [MTLValueTransformer
            transformerUsingForwardBlock:^id(NSString *string, BOOL *success, NSError **__attribute__((unused)) error) {
                if (string == nil) return nil;

                if (![string isKindOfClass:NSString.class]) {
                    *success = NO;
                    return nil;
                }

                NSUUID *result = [[NSUUID alloc] initWithUUIDString:string];

                if (result == nil) {
                    *success = NO;
                    return nil;
                }

                return result;
            }
                            reverseBlock:^id(NSUUID *uuid, BOOL *success, NSError **__attribute__((unused)) error) {
                                if (uuid == nil) return nil;

                                if (![uuid isKindOfClass:NSUUID.class]) {
                                    *success = NO;
                                    return nil;
                                }

                                return uuid.UUIDString.localizedLowercaseString;
                            }];
}

@end
