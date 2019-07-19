//
//  DoraemonDatabasePlugin.m
//  AFNetworking
//
//  Created by wentian on 2019/7/11.
//

#import "DoraemonDatabasePlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonDatabaseViewController.h"

@implementation DoraemonDatabasePlugin

- (void)pluginDidLoad{
    DoraemonDatabaseViewController *vc = [[DoraemonDatabaseViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
