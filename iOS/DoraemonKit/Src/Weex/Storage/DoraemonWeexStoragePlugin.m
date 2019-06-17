//
//  DoraemonWeexStoragePlugin.m
//  WeexDemo
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStoragePlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonWeexStorageViewController.h"

@implementation DoraemonWeexStoragePlugin

- (void)pluginDidLoad{
    DoraemonWeexStorageViewController *vc = [[DoraemonWeexStorageViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
