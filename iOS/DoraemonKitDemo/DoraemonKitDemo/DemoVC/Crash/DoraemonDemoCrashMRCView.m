//
//  DoraemonDemoCrashMRCView.m
//  DoraemonKitDemo
//
//  Created by wenquan on 2018/11/22.
//  Copyright © 2018 yixiang. All rights reserved.
//

#import "DoraemonDemoCrashMRCView.h"

@implementation DoraemonDemoCrashMRCView

- (instancetype)init {
    self = [super init];
    if (self) {
        UIView *tempView = [[UIView alloc]init];
        //[tempView release];
        
        //对象已经被释放，内存不合法，此块内存地址又没被覆盖，所以此内存内垃圾内存，所以调用方法的时候会导致SIGSEGV的错误
        [tempView setNeedsDisplay];
    }
    return self;
}

@end
