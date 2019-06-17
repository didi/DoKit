//
//  DoraemonWeexInfoPlugin.m
//  WeexDemo
//
//  Created by yixiang on 2019/6/4.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexInfoPlugin.h"
#import "DoraemonWeexInfoViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonWeexInfoPlugin

- (void)pluginDidLoad{
    DoraemonWeexInfoViewController *vc = [[DoraemonWeexInfoViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
