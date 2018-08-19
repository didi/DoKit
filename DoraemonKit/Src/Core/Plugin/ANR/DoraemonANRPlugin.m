//
//  DoraemonANRPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import "DoraemonANRPlugin.h"
#import "DoraemonANRViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonANRPlugin

- (void)pluginDidLoad{
    DoraemonANRViewController *vc = [[DoraemonANRViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
