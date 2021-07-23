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

@end

@interface UIGestureRecognizer (DoraemonMCSupport)

@property (nonatomic , strong , readonly) NSMutableArray<DoraemonMCGestureTargetActionPair *> *do_mc_targetActionPairs;

- (void)do_mc_manual_doAction ;

- (void)do_mc_handleGestureSend:(id)sender;

@property (nonatomic , assign) CGPoint do_mc_temp_p;

@property (nonatomic , assign) CGPoint do_mc_temp_location;

@property (nonatomic , assign) CGPoint do_mc_host_temp_p;


@property (nonatomic , assign) CGPoint do_mc_temp_Vol;

@end

@interface UIApplication (DoraemonMCSupport)

@end

@interface UIPanGestureRecognizer (DoraemonMCSupport)

@end

@interface UIControl (DoraemonMCSupport)

@end

