//
//  DoraemonMCClient.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCClient.h"
#import <SocketRocket/SocketRocket.h>
#import "DoraemonToastUtil.h"
#import "DoraemonMCCommandExcutor.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonManager.h"
#import "UIColor+Doraemon.h"

@implementation DoraemonMCClient

+ (void)showToast:(NSString *)toastContent {
    if (![toastContent isKindOfClass:[NSString class]] ||
        toastContent.length == 0) {
        return;
    }
    UIWindow *currentWindow = nil;
    if ([DoraemonHomeWindow shareInstance].hidden) {
        currentWindow = [UIApplication sharedApplication].keyWindow;
    }else {
        currentWindow = [DoraemonHomeWindow shareInstance];
    }
    if ([NSThread currentThread].isMainThread) {
        [DoraemonToastUtil showToastBlack:toastContent inView:currentWindow];
    }else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [DoraemonToastUtil showToastBlack:toastContent inView:currentWindow];
        });
    }
}

@end
