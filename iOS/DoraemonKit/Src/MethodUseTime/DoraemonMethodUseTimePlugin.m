//
//  DoraemonMethodUseTimePlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimePlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonMethodUseTimeViewController.h"

@implementation DoraemonMethodUseTimePlugin

- (void)pluginDidLoad{
    DoraemonMethodUseTimeViewController *vc = [[DoraemonMethodUseTimeViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
