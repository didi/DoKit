//
//  DoraemonUIProfilePlugin.m
//  DoraemonKit
//
//  Created by xgb on 2019/8/1.
//

#import "DoraemonUIProfilePlugin.h"
#import "DoraemonUIProfileViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonUIProfilePlugin

- (void)pluginDidLoad{
    DoraemonUIProfileViewController *vc = [[DoraemonUIProfileViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
