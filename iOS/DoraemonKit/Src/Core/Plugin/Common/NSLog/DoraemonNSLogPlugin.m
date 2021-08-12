//
//  DoraemonNSLogPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2018/11/25.
//

#import "DoraemonNSLogPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonNSLogViewController.h"

@implementation DoraemonNSLogPlugin

- (void)pluginDidLoad{
    DoraemonNSLogViewController *vc = [[DoraemonNSLogViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
