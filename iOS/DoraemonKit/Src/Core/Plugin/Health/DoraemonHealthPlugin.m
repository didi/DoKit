//
//  DoraemonHealthPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/12/30.
//

#import "DoraemonHealthPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonHealthViewController.h"

@implementation DoraemonHealthPlugin

- (void)pluginDidLoad{
    DoraemonHealthViewController *vc = [[DoraemonHealthViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
