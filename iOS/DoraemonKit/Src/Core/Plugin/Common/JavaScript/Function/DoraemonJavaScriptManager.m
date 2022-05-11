//
//  DoraemonJavaScriptManager.m
//  DoraemonKit
//
//  Created by carefree on 2022/5/11.
//

#import "DoraemonJavaScriptManager.h"
#import <WebKit/WebKit.h>
#import "DoraemonDefine.h"
#import "DoraemonHomeWindow.h"
#import "UIViewController+Doraemon.h"
#import "DoraemonJavaScriptViewController.h"

@interface DoraemonJavaScriptManager ()

@property (nonatomic, weak) id  webView;

@end

@implementation DoraemonJavaScriptManager

+ (DoraemonJavaScriptManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonJavaScriptManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonJavaScriptManager alloc] init];
    });
    return instance;
}

- (void)show {
    NSArray *webViews = [DoraemonUtil getWebViews];
    
    NSString *title = DoraemonLocalizedString(@"请选择WebView");
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:title preferredStyle:UIAlertControllerStyleActionSheet];
    for (NSInteger i = 0; i < webViews.count; i++) {
        WKWebView *webView = webViews[i];
        NSString *actionTitle = webView.description;
        UIAlertAction *action = [UIAlertAction actionWithTitle:actionTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            [self selectWebView:webView];
        }];
        [alert addAction:action];
    }
    if (webViews.count == 0) {
        UIAlertAction *action = [UIAlertAction actionWithTitle:DoraemonLocalizedString(@"无可用的WebView") style:UIAlertActionStyleDestructive handler:nil];
        action.enabled = NO;
        [alert addAction:action];
    }
    if (webViews.count == 1) {
        //只有一个，则跳过选择
        [self selectWebView:webViews.firstObject];
        return;
    }
    [alert addAction:[UIAlertAction actionWithTitle:DoraemonLocalizedString(@"取消") style:UIAlertActionStyleCancel handler:nil]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [UIViewController.topViewControllerForKeyWindow presentViewController:alert animated:YES completion:nil];
    });
}

- (void)selectWebView:(id)webView {
    self.webView = webView;
    DoraemonJavaScriptViewController *vc = [[DoraemonJavaScriptViewController alloc] init];
    [DoraemonHomeWindow openPlugin:vc];
}

- (void)evalJavaScript:(NSString *)script {
    id currentWebView = self.webView;
    if (!currentWebView) {
        return;
    }
    if ([currentWebView isKindOfClass:WKWebView.class]) {
        WKWebView *webView = currentWebView;
        [webView evaluateJavaScript:script completionHandler:^(id _Nullable result, NSError * _Nullable error) {
            if (error) {
                NSLog(@"js error: %@", error);
            }
        }];
    }
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wdeprecated-declarations"
    if ([currentWebView isKindOfClass:UIWebView.class]) {
        UIWebView *webView = currentWebView;
        [webView stringByEvaluatingJavaScriptFromString:script];
    }
#pragma clang diagnostic pop
}

@end
