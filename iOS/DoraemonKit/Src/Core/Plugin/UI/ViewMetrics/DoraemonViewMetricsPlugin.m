//
//  DoraemonViewMetricsPlugin.m
//  DoraemonKit
//
//  Created by xgb on 2018/12/11.
//

#import "DoraemonViewMetricsPlugin.h"
#import "DoraemonMetricsViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonViewMetricsPlugin

- (void)pluginDidLoad{
    DoraemonMetricsViewController *vc = [[DoraemonMetricsViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
