//
//  DoraemonAllTestPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/24.
//

#import "DoraemonAllTestPlugin.h"
#import "DoraemonAllTestViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonAllTestPlugin

- (void)pluginDidLoad{
    DoraemonAllTestViewController *vc = [[DoraemonAllTestViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
