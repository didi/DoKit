//
//  DoraemonCocoaLumberjackPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/4.
//

#import "DoraemonCocoaLumberjackPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonCocoaLumberjackViewController.h"

@implementation DoraemonCocoaLumberjackPlugin

- (void)pluginDidLoad{
    DoraemonCocoaLumberjackViewController *vc = [[DoraemonCocoaLumberjackViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
