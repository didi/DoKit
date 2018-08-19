//
//  DoraemonLoggerPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/12.
//

#import "DoraemonLoggerPlugin.h"
#import "DoraemonLoggerViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonLoggerPlugin

- (void)pluginDidLoad{
    DoraemonLoggerViewController *vc = [[DoraemonLoggerViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
