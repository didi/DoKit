//
//  DoraemonWeexStoragePlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStoragePlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonWeexStorageViewController.h"

@implementation DoraemonWeexStoragePlugin

- (void)pluginDidLoad{
    DoraemonWeexStorageViewController *vc = [[DoraemonWeexStorageViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
