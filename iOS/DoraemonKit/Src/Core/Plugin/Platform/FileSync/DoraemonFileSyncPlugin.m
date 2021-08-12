//
//  DoraemonFileSyncPlugin.m
//  DoraemonKit
//
//  Created by didi on 2020/6/10.
//

#import "DoraemonFileSyncPlugin.h"
#import "DoraemonManager.h"
#import "DoraemonFileSyncViewController.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonToastUtil.h"
#import "UIViewController+Doraemon.h"
#import "Doraemoni18NUtil.h"

@implementation DoraemonFileSyncPlugin

- (void)pluginDidLoad{
    if ([DoraemonManager shareInstance].pId) {
        DoraemonFileSyncViewController *vc = [[DoraemonFileSyncViewController alloc] init];
        [DoraemonHomeWindow openPlugin:vc];
    }else{
        [DoraemonToastUtil showToastBlack:DoraemonLocalizedString(@"需要到www.dokit.cn上注册pId才能使用该功能") inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
    }
}

@end
