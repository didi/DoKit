//
//  DoraemonMemoryPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryPlugin.h"
#import "DoraemonMemoryViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonMemoryPlugin

- (void)pluginDidLoad{
    DoraemonMemoryViewController *vc = [[DoraemonMemoryViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
