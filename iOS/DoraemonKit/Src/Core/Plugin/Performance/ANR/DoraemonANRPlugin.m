//
//  DoraemonANRPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import "DoraemonANRPlugin.h"
#import "DoraemonANRViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonANRPlugin

- (void)pluginDidLoad{
    DoraemonANRViewController *vc = [[DoraemonANRViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
