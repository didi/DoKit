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
  [[DoraemonManager shareInstance] addPluginWithTitle:@"一机多控"
                                                 icon:@"doraemon_default"
                                                 desc:@"一机多控入口"
                                           pluginName:@"一机多控"
                                             atModule:@"常用工具"
                                               handle:^(NSDictionary * _Nonnull itemData) {
      
      UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"" message:nil preferredStyle:UIAlertControllerStyleActionSheet];
      
      UIAlertAction *masterAction = nil;
      UIAlertAction *otherAction = nil;
      if ([DoraemonMCServer isOpen]) {
          otherAction = [UIAlertAction actionWithTitle:@"展示主机信息" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
              DoraemonMCViewController *toolVC = [DoraemonMCViewController instanceWithType:DoraemonMCViewControllerTypeServer];
              [DoraemonHomeWindow openPlugin:toolVC];
          }];
          masterAction = [UIAlertAction actionWithTitle:[NSString stringWithFormat:@"关闭主机服务(从机数:%zd)",[DoraemonMCServer connectCount]] style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
              [DoraemonMCServer close];
          }];
      }else if ([DoraemonMCClient isConnected]) {
          otherAction = [UIAlertAction actionWithTitle:@"断开连接" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
              [DoraemonMCClient disConnect];
          }];
      }
      else {
          masterAction = [UIAlertAction actionWithTitle:@"指定为主机" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
              DoraemonMCViewController *toolVC = [DoraemonMCViewController instanceWithType:DoraemonMCViewControllerTypeServer];
              [DoraemonHomeWindow openPlugin:toolVC];
          }];
          otherAction = [UIAlertAction actionWithTitle:@"连接主机" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
              DoraemonMCViewController *toolVC = [DoraemonMCViewController instanceWithType:DoraemonMCViewControllerTypeClient];
              [DoraemonHomeWindow openPlugin:toolVC];

          }];
      }

      
      UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {}];
      if (masterAction) {
          [alert addAction:masterAction];
      }
      if (otherAction) {
          [alert addAction:otherAction];
      }
      [alert addAction:cancel];
      
      [DoraemonHomeWindow.shareInstance.nav presentViewController:alert animated:YES completion:nil];
      
  }];
}

@end
