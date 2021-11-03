//
//  DoraemonGPSPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2017/12/20.
//

#import "DoraemonGPSPlugin.h"
#import "DoraemonGPSViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonGPSPlugin

- (void)pluginDidLoad{
    DoraemonGPSViewController *vc = [[DoraemonGPSViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
