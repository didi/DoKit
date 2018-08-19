//
//  DoraemonClosePlugin.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/27.
//

#import "DoraemonClosePlugin.h"
#import "DoraemonManager.h"
#import "DoraemonUtil.h"
#import "DoraemonHomeWindow.h"

@implementation DoraemonClosePlugin

- (void)pluginDidLoad{
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:@"提示" message:@"Doraemon关闭之后需要重启App才能重新打开" preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:@"取消" style:UIAlertActionStyleCancel handler:nil];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:@"确定" style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        [[DoraemonManager shareInstance] hiddenDoraemon];
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [[DoraemonUtil topViewControllerForKeyWindow] presentViewController:alertController animated:YES completion:nil];
    
    [[DoraemonHomeWindow shareInstance] hide];
}

@end
