//
//  DoraemonMockPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/10/23.
//

#import "DoraemonMockPlugin.h"
#import "DoraemonMockViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonMockPlugin

- (void)pluginDidLoad{
    DoraemonMockViewController *vc = [[DoraemonMockViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
