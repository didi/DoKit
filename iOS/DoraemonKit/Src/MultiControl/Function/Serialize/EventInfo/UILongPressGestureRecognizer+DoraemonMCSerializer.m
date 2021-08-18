//
//  UILongPressGestureRecognizer+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UILongPressGestureRecognizer+DoraemonMCSerializer.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import <objc/runtime.h>
#import "NSObject+DoraemonMCSupport.h"

@implementation UILongPressGestureRecognizer (DoraemonMCSerializer)


/// 当前手势对象的信息转为字典
- (NSDictionary *)do_mc_serialize_dictionary {
    return [super do_mc_serialize_dictionary];
}

/// 将字典中的值同步到当前手势对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary {
    [super do_mc_serialize_syncInfoWithDictionary:dictionary];
}

#pragma mark - hook

+ (void)load {

    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(locationInView:) swizzledSel:@selector(do_mc_longPress_locationInView:)];
    });
}

- (CGPoint)do_mc_longPress_locationInView:(UIView *)view{
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_location_at_host)) {
        return [self do_mc_longPress_locationInView:view];
    }
    return self.do_mc_location_at_host;
}


@end
