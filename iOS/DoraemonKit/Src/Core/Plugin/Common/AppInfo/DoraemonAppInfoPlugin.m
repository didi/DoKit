//
//  DoraemonAppInfoPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonAppInfoPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonAppInfoViewController.h"

@implementation DoraemonAppInfoPlugin

- (void)pluginDidLoad{
    DoraemonAppInfoViewController *vc = [[DoraemonAppInfoViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
