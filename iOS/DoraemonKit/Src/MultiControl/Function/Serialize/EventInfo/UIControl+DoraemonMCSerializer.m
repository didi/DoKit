//
//  UIControl+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UIControl+DoraemonMCSerializer.h"

NSString const *kUIControlDoraemonMCSerializerWrapperKey = @"ctlrapper";

static NSString const *kUIControlDoraemonMCSerializerStateKey = @"state";

@implementation UIControl (DoraemonMCSerializer)

/// 当前对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary {
    return @{
        kUIControlDoraemonMCSerializerWrapperKey : @{
                kUIControlDoraemonMCSerializerStateKey : @(self.state)
        }
    };
}

/// 将字典中的值同步到当前对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary {    
    if (![dictionary isKindOfClass:[NSDictionary class]]) {
        return;
    }
    NSDictionary *map = dictionary[kUIControlDoraemonMCSerializerWrapperKey];
    
    if (![map isKindOfClass:[NSDictionary class]]) {
        return;
    }
    
    NSNumber *state = map[kUIControlDoraemonMCSerializerStateKey];
    
    if (![state isKindOfClass:[NSNumber class]]) {
        return;
    }
    
    UIControlState stateFromDict = state.intValue;
    
    [self setHighlighted:(stateFromDict & UIControlStateHighlighted)];

    [self setSelected:(stateFromDict & UIControlStateSelected)];

    [self setEnabled:(stateFromDict & UIControlStateDisabled)];
}

@end
