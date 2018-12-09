//
//  DoraemonCrashPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/19.
//

#import "DoraemonCrashPlugin.h"
#import "DoraemonCrashViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonCrashPlugin

- (void)pluginDidLoad{
    DoraemonCrashViewController *vc = [[DoraemonCrashViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
