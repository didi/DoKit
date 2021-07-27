//
//  DoraemonMCEventCapturer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <UIKit/UIKit.h>


@interface DoraemonMCGestureTargetActionPair : NSObject

@property (nonatomic , weak ) id target ;
@property (nonatomic , assign) SEL action ;
@property (nonatomic , weak) id sender;
- (instancetype)initWithTarget:(id)target action:(SEL)action sender:(id)sender;
- (BOOL)isEqualToTarget:(id)target andAction:(SEL)action;

- (BOOL)valid;

- (void)doAction ;

@end


@interface NSObject (DoraemonMCSupport)

/**
 swizzle 类方法
 
 @param oriSel 原有的方法
 @param swiSel swizzle的方法
 */
+ (void)do_mc_swizzleClassMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel;

/**
 swizzle 实例方法
 
 @param oriSel 原有的方法
 @param swiSel swizzle的方法
 */
+ (void)do_mc_swizzleInstanceMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel;

+ (void)do_mc_swizzleInstanceMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel cls:(Class)cls;

/// 获取key对应的关联对象的值 自动解包成CGPoint
- (CGPoint)do_mc_point_value_forkey:(const void * _Nonnull)key ;

/// 获取key对应的关联对象的值 自动解包成CGRect
- (CGRect)do_mc_rect_value_forkey:(const void * _Nonnull)key ;
@end

@interface UIGestureRecognizer (DoraemonMCSupport)

@property (nonatomic , strong , readonly) NSMutableArray<DoraemonMCGestureTargetActionPair *> *do_mc_targetActionPairs;

- (void)do_mc_manual_doAction ;

- (void)do_mc_handleGestureSend:(id)sender;

/// 主机上 手势的触摸坐标
@property (nonatomic , assign) CGPoint do_mc_location_at_host;


@end

@interface UIApplication (DoraemonMCSupport)

@end

@interface UIPanGestureRecognizer (DoraemonMCSupport)

/// 主机上 平移手势的偏移距离
@property (nonatomic , assign) CGPoint do_mc_translation_at_host;
/// 主机上平移手势的加速度
@property (nonatomic , assign) CGPoint do_mc_vol_at_host;

@end

@interface UILongPressGestureRecognizer (DoraemonMCSupport)

@end

@interface UIControl (DoraemonMCSupport)

@end

