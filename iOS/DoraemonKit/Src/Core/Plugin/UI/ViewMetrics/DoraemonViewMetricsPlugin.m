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


//plugin初始化一个VC， 再将其替换为home的根VC
- (void)pluginDidLoad{
    DoraemonMetricsViewController *vc = [[DoraemonMetricsViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
