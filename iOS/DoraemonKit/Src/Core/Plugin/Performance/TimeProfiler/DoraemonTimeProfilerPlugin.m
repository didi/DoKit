//
//  DoraemonTimeProfilerPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/10/15.
//

#import "DoraemonTimeProfilerPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonTimeProfilerViewController.h"

@implementation DoraemonTimeProfilerPlugin

- (void)pluginDidLoad{
    DoraemonTimeProfilerViewController *vc = [[DoraemonTimeProfilerViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
