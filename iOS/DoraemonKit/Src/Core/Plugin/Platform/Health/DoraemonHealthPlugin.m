//
//  DoraemonHealthPlugin.m
//  DoraemonKit
//
//  Created by didi on 2019/12/30.
//

#import "DoraemonHealthPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonHealthViewController.h"
#import "DoraemonManager.h"
#import "DoraemonToastUtil.h"
#import "UIViewController+Doraemon.h"
#import "Doraemoni18NUtil.h"

@implementation DoraemonHealthPlugin

- (void)pluginDidLoad{
    if ([DoraemonManager shareInstance].pId) {
        DoraemonHealthViewController *vc = [[DoraemonHealthViewController alloc] init];
        [DoraemonHomeWindow openPlugin:vc];
    }else{
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"需要到www.dokit.cn上注册pId才能使用该功能") inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
    }
}

@end
