//
//  UIResponder+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UIResponder+DoraemonMCSerializer.h"

NSString const *kUIResponderDoraemonMCSerializerWrapperKey = @"resp_wrapper";

static NSString const *kUIResponderDoraemonMCSerializerFirstResponderKey = @"firstResp";


@implementation UIResponder (DoraemonMCSerializer)

/// 当前对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary {
    return @{
        kUIResponderDoraemonMCSerializerWrapperKey : @{
                kUIResponderDoraemonMCSerializerFirstResponderKey : @(self.isFirstResponder)
        }
    };
}

/// 将字典中的值同步到当前对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary {
    if (![dictionary isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSDictionary *map = dictionary[kUIResponderDoraemonMCSerializerWrapperKey];
    
    if (![map isKindOfClass:[NSDictionary class]]) {
        return;
    }
    
    NSNumber *firstResponderNumber = map[kUIResponderDoraemonMCSerializerFirstResponderKey];
    
    if (![firstResponderNumber isKindOfClass:[NSNumber class]]) {
        return;
    }
    
    if (self.isFirstResponder &&
        NO == firstResponderNumber.boolValue) {
        [self resignFirstResponder];
    }else if (NO == self.isFirstResponder && firstResponderNumber.boolValue) {
        [self becomeFirstResponder];
    }
}

@end
