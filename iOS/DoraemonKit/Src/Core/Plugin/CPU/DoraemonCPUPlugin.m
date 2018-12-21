//
//  DoraemonCPUPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/12.
//

#import "DoraemonCPUPlugin.h"
#import "DoraemonCPUViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonCPUPlugin

- (void)pluginDidLoad{
    DoraemonCPUViewController *vc = [[DoraemonCPUViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
