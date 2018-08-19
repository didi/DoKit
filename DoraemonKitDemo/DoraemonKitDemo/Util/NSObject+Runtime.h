//
//  NSObject+Runtime.h
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/1/30.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface NSObject (Runtime)

/**
 swizzle 类方法
 
 @param oriSel 原有的方法
 @param swiSel swizzle的方法
 */
+ (void)swizzleClassMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel;

/**
 swizzle 实例方法
 
 @param oriSel 原有的方法
 @param swiSel swizzle的方法
 */
+ (void)swizzleInstanceMethodWithOriginSel:(SEL)oriSel swizzledSel:(SEL)swiSel;

@end
