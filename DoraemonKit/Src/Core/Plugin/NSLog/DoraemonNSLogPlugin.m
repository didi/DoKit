//
//  DoraemonNSLogPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/25.
//

#import "DoraemonNSLogPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonNSLogViewController.h"

@implementation DoraemonNSLogPlugin

- (void)pluginDidLoad{
    DoraemonNSLogViewController *vc = [[DoraemonNSLogViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
