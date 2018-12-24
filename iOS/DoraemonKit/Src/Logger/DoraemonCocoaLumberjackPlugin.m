//
//  DoraemonCocoaLumberjackPlugin.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/4.
//

#import "DoraemonCocoaLumberjackPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonCocoaLumberjackViewController.h"

@implementation DoraemonCocoaLumberjackPlugin

- (void)pluginDidLoad{
    DoraemonCocoaLumberjackViewController *vc = [[DoraemonCocoaLumberjackViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
