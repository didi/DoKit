//
//  DoraemonLargeImagePlugin.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonLargeImagePlugin.h"
#import "DoraemonLargeImageViewController.h"
#import "DoraemonUtil.h"

@implementation DoraemonLargeImagePlugin
- (void)pluginDidLoad {
    DoraemonLargeImageViewController *vc = [[DoraemonLargeImageViewController alloc] init];
    [DoraemonUtil openPlugin:vc];
}

@end
