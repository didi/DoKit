//
//  DoraemonLargeImagePlugin.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonLargeImagePlugin.h"
#import "DoraemonLargeImageViewController.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonLargeImagePlugin
- (void)pluginDidLoad {
    DoraemonLargeImageViewController *vc = [[DoraemonLargeImageViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

@end
