//
//  DoraemonPageTimePlugin.m
//  DoraemonKit
//
//  Created by Frank on 2020/5/27.
//

#import "DoraemonPageTimePlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonCPUViewController.h"

@implementation DoraemonPageTimePlugin
- (void)pluginDidLoad{
    DoraemonCPUViewController *vc = [[DoraemonCPUViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
