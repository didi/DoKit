//
//  RelativePositionPlugin.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/26.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "RelativePositionPlugin.h"
#import "RelativePositionManager.h"
#import "DoraemonHomeWindow.h"

@implementation RelativePositionPlugin

- (void)pluginDidLoad{
    //singleton 单例，关闭HomeWindow,打开RelativePostionView;
    [[RelativePositionManager shareInstance] show];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
