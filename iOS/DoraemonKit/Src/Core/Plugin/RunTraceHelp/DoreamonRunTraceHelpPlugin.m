//
//  DoreamonRunTraceHelp.m
//  DoraemonKit-DoraemonKit
//
//  Created by S S on 2019/5/9.
//

#import "DoreamonRunTraceHelpPlugin.h"
#import "DoraemonRunTraceHelpManager.h"
#import "DoraemonHomeWindow.h"

@implementation DoreamonRunTraceHelpPlugin

- (void)pluginDidLoad{
    [[DoraemonRunTraceHelpManager shareInstance] show];
    [[DoraemonHomeWindow shareInstance] hide];
}


@end
