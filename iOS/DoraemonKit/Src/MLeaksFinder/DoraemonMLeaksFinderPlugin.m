//
//  DoraemonMLeaksFinderPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/10/6.
//

#import "DoraemonMLeaksFinderPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonMLeaksFinderViewController.h"

@implementation DoraemonMLeaksFinderPlugin

- (void)pluginDidLoad{
    DoraemonMLeaksFinderViewController *vc = [[DoraemonMLeaksFinderViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
