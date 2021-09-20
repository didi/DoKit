//
//  DoraemonMultiControlPlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMultiControlPlugin.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"
#import "DoraemonManager.h"
#import "DoraemonMCViewController.h"
#import "DoraemonMCClient.h"
#import "DoraemonMCServer.h"


@implementation DoraemonMultiControlPlugin

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(UIApplicationDidFinishLaunchingNotificationDeal) name:UIApplicationDidFinishLaunchingNotification object:nil];
    });
}


+ (void)UIApplicationDidFinishLaunchingNotificationDeal {
  [[DoraemonManager shareInstance] addPluginWithTitle:DoraemonLocalizedString(@"一机多控")
                                                 icon:@"dk_icon_mc"
                                                 desc:@"一机多控入口"
                                           pluginName:@"一机多控"
                                             atModule:DoraemonLocalizedString(@"平台工具")
                                               handle:^(NSDictionary * _Nonnull itemData) {
      DoraemonMCViewController *toolVC = [DoraemonMCViewController new];
      [DoraemonHomeWindow openPlugin:toolVC];
      
  }];
}

@end
