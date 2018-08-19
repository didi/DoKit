//
//  DoraemonSandboxPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import "DoraemonSandboxPlugin.h"
#import "DoraemonSandboxViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonSandboxPlugin

- (void)pluginDidLoad{
    DoraemonSandboxViewController *vc = [[DoraemonSandboxViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
