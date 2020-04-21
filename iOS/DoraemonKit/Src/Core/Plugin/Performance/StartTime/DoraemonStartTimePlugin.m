//
//  DoraemonStartTimePlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/7/17.
//

#import "DoraemonStartTimePlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonStartTimeViewController.h"

@implementation DoraemonStartTimePlugin

- (void)pluginDidLoad{
    DoraemonStartTimeViewController *vc = [[DoraemonStartTimeViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
