//
//  DoraemonGPSPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/20.
//

#import "DoraemonGPSPlugin.h"
#import "DoraemonGPSViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonGPSPlugin

- (void)pluginDidLoad{
    DoraemonGPSViewController *vc = [[DoraemonGPSViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
