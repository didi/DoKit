//
//  DoraemonFPSPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonFPSPlugin.h"
#import "DoraemonFPSViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonFPSPlugin

- (void)pluginDidLoad{
    DoraemonFPSViewController *vc = [[DoraemonFPSViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
