//
//  DoraemonJavaScriptPlugin.m
//  AFNetworking
//
//  Created by carefree on 2022/5/11.
//

#import "DoraemonJavaScriptPlugin.h"
#import "DoraemonJavaScriptManager.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonJavaScriptPlugin

- (void)pluginDidLoad {
    [[DoraemonHomeWindow shareInstance] hide];
    [[DoraemonJavaScriptManager shareInstance] show];
}

@end
