//
//  DoraemonAppSettingPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by didi on 2020/2/28.
//

#import "DoraemonAppSettingPlugin.h"
#import "DoraemonUtil.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonAppSettingPlugin

- (void)pluginDidLoad {
    [DoraemonUtil openAppSetting];
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
