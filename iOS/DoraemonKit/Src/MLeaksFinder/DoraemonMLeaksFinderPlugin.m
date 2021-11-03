//
//  DoraemonMLeaksFinderPlugin.m
//  DoraemonKit
//
//  Created by didi on 2019/10/6.
//

#import "DoraemonMLeaksFinderPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonMLeaksFinderViewController.h"

@implementation DoraemonMLeaksFinderPlugin

- (void)pluginDidLoad{
    DoraemonMLeaksFinderViewController *vc = [[DoraemonMLeaksFinderViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
