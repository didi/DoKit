//
//  DoraemonH5Plugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonH5Plugin.h"
#import "DoraemonH5ViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonH5Plugin

- (void)pluginDidLoad{
    DoraemonH5ViewController *vc = [[DoraemonH5ViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
