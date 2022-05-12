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

@implementation DKCommonDTOModel

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
            @"requestId": @"pid",
            @"method": @"type",
            @"data": @"data",
            @"connectSerial": @"connectSerial",
            @"dataType": @"contentType"
    };
}

+ (NSValueTransformer *)connectSerialJSONTransformer {
    return [MTLValueTransformer
            transformerUsingForwardBlock:^id(NSString *string, BOOL *success, NSError **__attribute__((unused)) error) {
                if (string == nil) return nil;

                if (![string isKindOfClass:NSString.class]) {
//                    if (error) {
//                        NSDictionary *userInfo = @{
//                                NSLocalizedDescriptionKey: NSLocalizedString(@"Could not convert string to UUID", @""),
//                                NSLocalizedFailureReasonErrorKey: [NSString stringWithFormat:NSLocalizedString(@"Expected an NSString, got: %@.", @""), string],
//                                MTLTransformerErrorHandlingInputValueErrorKey: string
//                        };
//                        *error = [NSError errorWithDomain:MTLTransformerErrorHandlingErrorDomain code:MTLTransformerErrorHandlingErrorInvalidInput userInfo:userInfo];
//                    }
                    *success = NO;
                    return nil;
                }

                NSUUID *result = [[NSUUID alloc] initWithUUIDString:string];

                if (result == nil) {
//                    if (error) {
//                        NSDictionary *userInfo = @{
//                                NSLocalizedDescriptionKey: NSLocalizedString(@"Could not convert string to UUID", @""),
//                                NSLocalizedFailureReasonErrorKey: [NSString stringWithFormat:NSLocalizedString(@"Input UUID string %@ was malformed", @""), string],
//                                MTLTransformerErrorHandlingInputValueErrorKey: string
//                        };
//                        *error = [NSError errorWithDomain:MTLTransformerErrorHandlingErrorDomain code:MTLTransformerErrorHandlingErrorInvalidInput userInfo:userInfo];
//                    }
                    *success = NO;
                    return nil;
                }

                return result;
            }
                            reverseBlock:^id(NSUUID *uuid, BOOL *success, NSError **__attribute__((unused)) error) {
                                if (uuid == nil) return nil;

                                if (![uuid isKindOfClass:NSUUID.class]) {
//                                    if (error != NULL) {
//                                        NSDictionary *userInfo = @{NSLocalizedDescriptionKey: NSLocalizedString(@"Could not convert UUID to string", @""),
//                                                NSLocalizedFailureReasonErrorKey: [NSString stringWithFormat:NSLocalizedString(@"Expected an NSUUID, got: %@.", @""), uuid],
//                                                MTLTransformerErrorHandlingInputValueErrorKey: uuid};
//                                        *error = [NSError errorWithDomain:MTLTransformerErrorHandlingErrorDomain code:MTLTransformerErrorHandlingErrorInvalidInput userInfo:userInfo];
//                                    }
                                    *success = NO;
                                    return nil;
                                }

                                return uuid.UUIDString.localizedLowercaseString;
                            }];
}

@end
