//
//  DoraemonMockPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/10/23.
//

#import "DoraemonMockPlugin.h"
#import "DoraemonMockViewController.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonManager.h"
#import "DoraemonToastUtil.h"
#import "UIViewController+Doraemon.h"

@implementation DoraemonMockPlugin

- (void)pluginDidLoad{
    if ([DoraemonManager shareInstance].pId) {
        DoraemonMockViewController *vc = [[DoraemonMockViewController alloc] init];
        [DoraemonHomeWindow openPlugin:vc];
    }else{
        [DoraemonToastUtil showToastBlack:@"需要到www.dokit.cn上注册pId才能使用该功能" inView:[UIViewController rootViewControllerForDoraemonHomeWindow].view];
    }

}

@end
