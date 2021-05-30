//
//  PluginA.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/4/24.
//  Copyright © 2021 yixiang. All rights reserved.
//


#import "PluginA.h"
#import "DoraemonViewAPluginManager.h"
#import "DoraemonInfoWindow.h"
#import "DoraemonHomeWindow.h"

@implementation PluginA

- (void)pluginDidLoad{
    //singleton 单例，关闭HomeWindow,打开checkwINOW;
    NSLog(@"我的插件添加了");
    [[DoraemonViewAPluginManager shareInstance] showA];
    [[DoraemonViewAPluginManager shareInstance] showB];
    //[[DoraemonInfoWindow shareInstance] show];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
