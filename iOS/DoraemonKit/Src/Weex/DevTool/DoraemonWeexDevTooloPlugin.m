//
//  DoraemonWeexDevTooloPlugin.m
//  WeexDemo
//
//  Created by yixiang on 2019/6/6.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexDevTooloPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonWeexDevToolViewController.h"

@implementation DoraemonWeexDevTooloPlugin

- (void)pluginDidLoad{
    DoraemonWeexDevToolViewController *vc = [[DoraemonWeexDevToolViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
