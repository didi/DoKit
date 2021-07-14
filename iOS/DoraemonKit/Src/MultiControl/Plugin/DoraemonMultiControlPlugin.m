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

@implementation DoraemonMultiControlPlugin

+ (void)load {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(UIApplicationDidFinishLaunchingNotificationDeal) name:UIApplicationDidFinishLaunchingNotification object:nil];
    });
}


+ (void)UIApplicationDidFinishLaunchingNotificationDeal {
  [[DoraemonManager shareInstance] addPluginWithTitle:@"一机多控"
                                                 icon:@"doraemon_default"
                                                 desc:@"一机多控入口"
                                           pluginName:@"一机多控"
                                             atModule:@"常用工具"
                                               handle:^(NSDictionary * _Nonnull itemData) {
      
      UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:nil preferredStyle:UIAlertControllerStyleActionSheet];
      
      UIAlertAction *master = [UIAlertAction actionWithTitle:@"指定为主机" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
          DoraemonMCViewController *toolVC = [DoraemonMCViewController instanceWithType:DoraemonMCViewControllerTypeServer];
          [DoraemonHomeWindow openPlugin:toolVC];
      }];
      
      UIAlertAction *other = [UIAlertAction actionWithTitle:@"连接主机" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
          DoraemonMCViewController *toolVC = [DoraemonMCViewController instanceWithType:DoraemonMCViewControllerTypeClient];
          [DoraemonHomeWindow openPlugin:toolVC];

      }];
      
      UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {}];
      
      [alert addAction:master];
      [alert addAction:other];
      [alert addAction:cancel];
      
      [DoraemonHomeWindow.shareInstance.nav presentViewController:alert animated:YES completion:nil];
      
  }];
}

@end
