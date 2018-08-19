//
//  DoraemonColorPickPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/5.
//

#import "DoraemonColorPickPlugin.h"
#import "DoraemonColorPickWindow.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonColorPickPlugin

- (void)pluginDidLoad{
    [[DoraemonColorPickWindow shareInstance] show];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
