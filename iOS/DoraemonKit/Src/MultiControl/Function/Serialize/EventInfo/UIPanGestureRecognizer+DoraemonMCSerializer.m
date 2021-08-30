//
//  UIPanGestureRecognizer+DoraemonMCSerializer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "UIPanGestureRecognizer+DoraemonMCSerializer.h"
#import "UIGestureRecognizer+DoraemonMCSerializer.h"
#import "DoraemonMCEventCapturer.h"
#import <objc/runtime.h>
#import "NSObject+DoraemonMCSupport.h"
#import <objc/message.h>

static NSString const *kUIPanGestureRecognizerTranslateKey = @"panTranslate";

static NSString const *kUIPanGestureRecognizerTranslateXKey = @"x";

static NSString const *kUIPanGestureRecognizerTranslateYKey = @"y";

static NSString const *kUIPanGestureRecognizerVelocityKey = @"panVelocity";

static NSString const *kUIPanGestureRecognizerVelocityXKey = @"x";

static NSString const *kUIPanGestureRecognizerVelocityYKey = @"y";

@implementation UIPanGestureRecognizer (DoraemonMCSerializer)

#pragma mark - serialize
- (void)do_mc_serialize_setupDictionary:(NSMutableDictionary *)dictM {
    [super do_mc_serialize_setupDictionary:dictM];
    
    CGFloat screenWidth = [UIScreen mainScreen].bounds.size.width;
    CGFloat screenHeight = [UIScreen mainScreen].bounds.size.height;
    CGPoint translationP = [self translationInView:self.view];
    CGPoint velocityP = [self velocityInView:self.view];

    dictM[kUIPanGestureRecognizerTranslateKey] = @{
        kUIPanGestureRecognizerTranslateXKey : @(translationP.x/screenWidth),
        kUIPanGestureRecognizerTranslateYKey : @(translationP.y/screenHeight),
    };
    
    dictM[kUIPanGestureRecognizerVelocityKey] = @{
        kUIPanGestureRecognizerVelocityXKey : @(velocityP.x/screenWidth),
        kUIPanGestureRecognizerVelocityYKey : @(velocityP.y/screenHeight),
    };
    
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
    
    CGFloat screenWidth = [UIScreen mainScreen].bounds.size.width;
    CGFloat screenHeight = [UIScreen mainScreen].bounds.size.height;
    NSDictionary *translateMap = map[kUIPanGestureRecognizerTranslateKey];
    CGPoint translation = CGPointMake([translateMap[kUIPanGestureRecognizerTranslateXKey] doubleValue] * screenWidth ,
                                      [translateMap[kUIPanGestureRecognizerTranslateYKey] doubleValue] * screenHeight);

    NSDictionary *velocityMap = map[kUIPanGestureRecognizerVelocityKey];
    CGPoint velocity = CGPointMake([velocityMap[kUIPanGestureRecognizerVelocityXKey] doubleValue] * screenWidth,
                                   [velocityMap[kUIPanGestureRecognizerVelocityYKey] doubleValue] * screenHeight);
    
    if (self.state == UIGestureRecognizerStateEnded ||
        self.state == UIGestureRecognizerStateCancelled) {
        self.do_mc_translation_at_host = CGPointZero;
    }else {
        self.do_mc_translation_at_host = translation;
        self.do_mc_vol_at_host = velocity;
    }
        
}

#pragma mark - hook

+ (void)load {

    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(translationInView:) swizzledSel:@selector(do_mc_pan_translationInView:)];
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(velocityInView:) swizzledSel:@selector(do_mc_pan_velocityInView:)];
        [self do_mc_swizzleInstanceMethodWithOriginSel:@selector(locationInView:) swizzledSel:@selector(do_mc_pan_locationInView:)];
    });
}


- (void)setDo_mc_vol_at_host:(CGPoint)do_mc_vol_at_host
{
    objc_setAssociatedObject(self, @selector(do_mc_vol_at_host), [NSValue valueWithCGPoint:do_mc_vol_at_host], OBJC_ASSOCIATION_RETAIN);
}

- (CGPoint)do_mc_vol_at_host {
    return [self do_mc_point_value_forkey:_cmd];
}


- (void)setDo_mc_translation_at_host:(CGPoint)do_mc_translation_at_host {
    objc_setAssociatedObject(self, @selector(do_mc_translation_at_host), [NSValue valueWithCGPoint:do_mc_translation_at_host], OBJC_ASSOCIATION_RETAIN);
}

- (CGPoint)do_mc_translation_at_host {
    return [self do_mc_point_value_forkey:_cmd];
}

- (CGPoint)do_mc_pan_translationInView:(UIView *)view {
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_translation_at_host)) {
        return [self do_mc_pan_translationInView:view];
    }
    return self.do_mc_translation_at_host;
}

- (CGPoint)do_mc_pan_velocityInView:(UIView *)view {
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_vol_at_host)) {
        return [self do_mc_pan_velocityInView:view];
    }
    return self.do_mc_vol_at_host;
}

- (CGPoint)do_mc_pan_locationInView:(UIView *)view {
    if (CGPointEqualToPoint(CGPointZero, self.do_mc_location_at_host)) {
        return  [self do_mc_pan_locationInView:view];
    }
    return self.do_mc_location_at_host;
}

- (void)do_mc_clear_all_value_at_host{
    [super do_mc_location_at_host];
    self.do_mc_translation_at_host = CGPointZero;
    self.do_mc_vol_at_host = CGPointZero;
}

@end
