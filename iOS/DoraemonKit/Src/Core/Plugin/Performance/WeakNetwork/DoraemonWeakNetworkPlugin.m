//
//  DoraemonWeakNetworkPlugin.m
//  AFNetworking
//
//  Created by didi on 2019/11/21.
//

#import "DoraemonWeakNetworkPlugin.h"
#import "DoraemonWeakNetworkViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonWeakNetworkPlugin

- (void)pluginDidLoad{
    DoraemonWeakNetworkViewController *vc = [[DoraemonWeakNetworkViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
