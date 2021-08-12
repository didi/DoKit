//
//  DoraemonMemoryOscillogramWindow.m
//  DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryOscillogramWindow.h"
#import "DoraemonMemoryOscillogramViewController.h"

@implementation DoraemonMemoryOscillogramWindow

+ (DoraemonMemoryOscillogramWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonMemoryOscillogramWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonMemoryOscillogramWindow alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (void)addRootVc{
    DoraemonMemoryOscillogramViewController *vc = [[DoraemonMemoryOscillogramViewController alloc] init];
    self.rootViewController = vc;
    self.vc = vc;
}

@end
