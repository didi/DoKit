//
//  DKDTOModel.m
//  DoraemonKit
//
//  Created by 唐佳诚 on 2022/4/28.
//

#import "DKDTOModel.h"

@implementation DKDTOModel

+ (NSDictionary *)JSONKeyPathsByPropertyKey {
    return @{
        @"requestId": @"pid",
        @"method": @"type",
        @"data": @"data"
    };
}

@end
