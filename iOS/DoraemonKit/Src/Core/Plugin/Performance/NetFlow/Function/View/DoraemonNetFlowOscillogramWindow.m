//
//  DoraemonNetFlowOscillogramWindow.m
//  DoraemonKit
//
//  Created by yixiang on 2018/5/2.
//

#import "DoraemonNetFlowOscillogramWindow.h"
#import "DoraemonNetFlowOscillogramViewController.h"

@implementation DoraemonNetFlowOscillogramWindow

+ (DoraemonNetFlowOscillogramWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonNetFlowOscillogramWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetFlowOscillogramWindow alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (void)addRootVc{
    DoraemonNetFlowOscillogramViewController *vc = [[DoraemonNetFlowOscillogramViewController alloc] init];
    self.rootViewController = vc;
    self.vc = vc;
}

@end
