//
//  DoraemonSubThreadUICheckPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/9/12.
//

#import "DoraemonSubThreadUICheckPlugin.h"
#import "DoraemonSubThreadUICheckViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonSubThreadUICheckPlugin

- (void)pluginDidLoad{
    DoraemonSubThreadUICheckViewController *vc = [[DoraemonSubThreadUICheckViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
