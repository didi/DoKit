//
//  DoraemonDeleteLocalDataPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/22.
//

#import "DoraemonDeleteLocalDataPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonDeleteLocalDataViewController.h"

@implementation DoraemonDeleteLocalDataPlugin

- (void)pluginDidLoad{
    DoraemonDeleteLocalDataViewController *vc = [[DoraemonDeleteLocalDataViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
