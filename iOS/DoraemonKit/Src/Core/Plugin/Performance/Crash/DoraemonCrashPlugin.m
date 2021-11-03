//
//  DoraemonCrashPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2018/6/19.
//

#import "DoraemonCrashPlugin.h"
#import "DoraemonCrashViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonCrashPlugin

- (void)pluginDidLoad{
    DoraemonCrashViewController *vc = [[DoraemonCrashViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
