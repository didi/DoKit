//
//  DoraemonNSUserDefaultsPlugin.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import "DoraemonNSUserDefaultsPlugin.h"
#import "DoraemonNSUserDefaultsViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonNSUserDefaultsPlugin
- (void)pluginDidLoad{
    DoraemonNSUserDefaultsViewController *vc = [[DoraemonNSUserDefaultsViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
