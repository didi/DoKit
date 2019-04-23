//
//  DoraemonMethodUseTimePlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimePlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonMethodUseTimeViewController.h"

@implementation DoraemonMethodUseTimePlugin

- (void)pluginDidLoad{
    DoraemonMethodUseTimeViewController *vc = [[DoraemonMethodUseTimeViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
