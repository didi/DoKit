//
//  DoraemonWeexDevTooloPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2019/6/6.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexDevTooloPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonWeexDevToolViewController.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"

@implementation DoraemonWeexDevTooloPlugin

- (void)pluginDidLoad{
    if ([DoraemonAppInfoUtil isSimulator]) {
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"模拟器不支持扫码功能") inView:[DoraemonHomeWindow shareInstance]];
        return;
    }
    DoraemonWeexDevToolViewController *vc = [[DoraemonWeexDevToolViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
