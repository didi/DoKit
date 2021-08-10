//
//  UITapGestureRecognizer+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UITapGestureRecognizer+DoraemonMCSerializer.h"
#import "NSObject+DoraemonMCSupport.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"

@implementation UITapGestureRecognizer (DoraemonMCSerializer)

#pragma mark - hook

+ (void)load {

    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(locationInView:) swizzledSel:@selector(do_mc_tap_locationInView:)];
    });
}

- (CGPoint)do_mc_tap_locationInView:(UIView *)view{
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_location_at_host)) {
        return [self do_mc_tap_locationInView:view];
    }
    return self.do_mc_location_at_host;
}


@end
