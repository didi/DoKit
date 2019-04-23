//
//  DoraemonToastUtil.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonToastUtil.h"
#import "UIColor+Doraemon.h"
#import "UIView+Doraemon.h"

#import "Doraemoni18NUtil.h"
#import "DoraemonCellSwitch.h"


@implementation DoraemonToastUtil

+ (void)showToast:(NSString *)text{
    [DoraemonToastUtil showToast:text inView:[UIApplication sharedApplication].keyWindow];
}

+ (void)showToast:(NSString *)text inView:(UIView *)superView {
    if (!superView) {
        return;
    }
    
    UILabel *label = [[UILabel alloc] init];
    label.font = [UIFont systemFontOfSize:14];
    label.text = text;
    [label sizeToFit];
    //label.backgroundColor = [UIColor doraemon_colorWithString:@"#33000000"];
    label.textColor = [UIColor blackColor];
    label.frame = CGRectMake(superView.doraemon_width/2-label.doraemon_width/2, superView.doraemon_height/2-label.doraemon_height/2, label.doraemon_width, label.doraemon_height);
    [superView addSubview:label];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [label removeFromSuperview];
    });
    
}

+ (void)handleRestartActionWithVC:(UIViewController *)vc
                     restartBlock:(DoraemonHandleRestartActionBlock)restartBlock
                      cancleBlock:(DoraemonHandleRestartCancleActionBlock)cancleBlock
{
    UIAlertController * alertController = [UIAlertController alertControllerWithTitle:DoraemonLocalizedString(@"提示") message:DoraemonLocalizedString(@"该功能需要重启App才能生效") preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancelAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:^(UIAlertAction * _Nonnull action) {
        cancleBlock ? cancleBlock():nil;
    }];
    UIAlertAction *okAction = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"确定") style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
        restartBlock ? restartBlock():nil;
    }];
    [alertController addAction:cancelAction];
    [alertController addAction:okAction];
    [vc presentViewController:alertController animated:YES completion:nil];
}

@end
