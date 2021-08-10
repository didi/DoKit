//
//  NSObject+DoraemonMCSupport.h
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

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
- (CGPoint)do_mc_point_value_forkey:(const void *)key ;

/// 获取key对应的关联对象的值 自动解包成CGRect
- (CGRect)do_mc_rect_value_forkey:(const void *)key ;

@end

NS_ASSUME_NONNULL_END
