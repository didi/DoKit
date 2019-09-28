//
//  DoraemonAlertUtil.m
//  AFNetworking
//
//  Created by didi on 2019/8/27.
//

#import "DoraemonAlertUtil.h"
#import "Doraemoni18NUtil.h"

@implementation DoraemonAlertUtil

+ (void)handleAlertActionWithVC:(UIViewController *)vc
                        okBlock:(DoraemonAlertOKActionBlock)okBlock
                    cancleBlock:(DoraemonAlertCancleActionBlock)cancleBlock {
    [self handleAlertActionWithVC:vc text:DoraemonLocalizedString(@"该功能需要重启App才能生效") okBlock:okBlock cancleBlock:cancleBlock];
}


+ (void)handleAlertActionWithVC:(UIViewController *)vc
                           text:(NSString *)text
                        okBlock:(DoraemonAlertOKActionBlock)okBlock
                    cancleBlock:(DoraemonAlertCancleActionBlock)cancleBlock {
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:text preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        cancleBlock ? cancleBlock():nil;
    }];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        okBlock ? okBlock():nil;
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [vc presentViewController:alertController animated:YES completion:nil];
}

@end
