//
//  A+B.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2018/7/5.
//  Copyright © 2018年 yixiang. All rights reserved.
//

#import "A+B.h"
#import "NSObject+Runtime.h"

@implementation A (B)

+ (void)load{
    [self swizzleInstanceMethodWithOriginSel:@selector(methodA) swizzledSel:@selector(b_swizzedMethodA)];
}

- (void)b_swizzedMethodA{
    [self b_swizzedMethodA];
    NSLog(@"methodB");
}

@end
