//
//  DoraemonFPSPlugin.m
//  DoraemonKit
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonFPSPlugin.h"
#import "DoraemonFPSViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonFPSPlugin

- (void)pluginDidLoad{
    DoraemonFPSViewController *vc = [[DoraemonFPSViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
