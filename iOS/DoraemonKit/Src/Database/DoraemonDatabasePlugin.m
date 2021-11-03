//
//  DoraemonDatabasePlugin.m
//  DoraemonKit
//
//  Created by wentian on 2019/7/11.
//

#import "DoraemonDatabasePlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonDatabaseViewController.h"

@implementation DoraemonDatabasePlugin

- (void)pluginDidLoad{
    DoraemonDatabaseViewController *vc = [[DoraemonDatabaseViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
