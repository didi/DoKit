//
//  DoraemonAppInfoPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import "DoraemonAppInfoPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonAppInfoViewController.h"

@implementation DoraemonAppInfoPlugin

- (void)pluginDidLoad{
    DoraemonAppInfoViewController *vc = [[DoraemonAppInfoViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
