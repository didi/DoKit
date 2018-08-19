//
//  A+C.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/7/5.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "A+C.h"
#import "NSObject+Runtime.h"

@implementation A (C)

+ (void)load{
    [self swizzleInstanceMethodWithOriginSel:@selector(methodA) swizzledSel:@selector(c_swizzedMethodA)];
}

- (void)c_swizzedMethodA{
    [self c_swizzedMethodA];
    NSLog(@"methodC");
}

@end
