//
//  UIPinchGestureRecognizer+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UIPinchGestureRecognizer+DoraemonMCSerializer.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import "DoraemonMCEventCapturer.h"
#import "NSObject+DoraemonMCSupport.h"

static NSString const *kUIPinchGestureRecognizerScaleKey = @"pinchScale";


@implementation UIPinchGestureRecognizer (DoraemonMCSerializer)

- (void)do_mc_serialize_setupDictionary:(NSMutableDictionary *)dictM {
    [super do_mc_serialize_setupDictionary:dictM];
    dictM[kUIPinchGestureRecognizerScaleKey] = @(self.scale);
}

/// 将字典中的值同步到当前手势对象的属性参数
- (void)do_mc_serialize_syncInfoWithDictionary:(NSDictionary *)dictionary {
    [super do_mc_serialize_syncInfoWithDictionary:dictionary];
    
    if (![dictionary isKindOfClass:[NSDictionary class]]) {
        return;
    }
    
    NSDictionary *map = dictionary[kUIGestureRecognizerDoraemonMCSerializerWrapperKey];
    
    if (![map isKindOfClass:[NSDictionary class]]) {
        return;
    }
    
    self.scale = [map[kUIPinchGestureRecognizerScaleKey] doubleValue];
}

#pragma mark - hook

+ (void)load {

    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(locationInView:) swizzledSel:@selector(do_mc_pinch_locationInView:)];
    });
}

- (CGPoint)do_mc_pinch_locationInView:(UIView *)view{
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_location_at_host)) {
        return [self do_mc_pinch_locationInView:view];
    }
    return self.do_mc_location_at_host;
}


@end
