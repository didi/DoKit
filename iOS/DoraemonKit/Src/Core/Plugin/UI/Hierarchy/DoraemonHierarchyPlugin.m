//
//  DoraemonHierarchyPlugin.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyPlugin.h"
#import "DoraemonHierarchyWindow.h"
#import "DoraemonHierarchyHelper.h"
#import "DoraemonHomeWindow.h"


@implementation DoraemonHierarchyPlugin

- (void)pluginDidLoad {
    DoraemonHierarchyWindow *window = [[DoraemonHierarchyWindow alloc] init];
    [DoraemonHierarchyHelper shared].window = window;
    [window show];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
