//
//  DoraemonViewCheckPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import "DoraemonViewCheckPlugin.h"
#import "DoraemonViewCheckManager.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonViewCheckPlugin

- (void)pluginDidLoad{
    [[DoraemonViewCheckManager shareInstance] show];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
