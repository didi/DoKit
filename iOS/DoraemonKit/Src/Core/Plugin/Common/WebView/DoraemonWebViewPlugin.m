//
//  DoraemonWebViewPlugin.m
//  AFNetworking
//
//  Created by 刘骁阳 on 2020/9/27.
//

#import "DoraemonWebViewPlugin.h"
#import <WebKit/WebKit.h>
#import "DoraemonWebHelperViewController.h"
#import "DoraemonHomeWindow.h"

@interface DoraemonWebViewPlugin ()

@end

@implementation DoraemonWebViewPlugin

- (void)pluginDidLoad{
    UIViewController *vc = [self getCurrentVC];
    WKWebView *webView = [self findWebViewInView:vc.view];
    DoraemonWebHelperViewController *wvc = [[DoraemonWebHelperViewController alloc] init];
    wvc.webView = webView;
    [DoraemonHomeWindow openPlugin:wvc];
}

- (WKWebView *)findWebViewInView:(UIView *)view {
    WKWebView *webView = nil;
    
    for (UIView *sub in view.subviews) {
        if ([sub isKindOfClass:[WKWebView class]]) {
            webView = (WKWebView *)sub;
        }
        else {
            if (sub.subviews.count > 0) {
                webView = [self findWebViewInView:sub];;
            }
        }
        
        if (webView) {
            break;
        }
    }
    
    return webView;
}

//获取当前屏幕显示的viewcontroller
- (UIViewController *)getCurrentVC
{
    UIViewController *result = nil;
    
    // 获取默认的window
    UIWindow * window = [[UIApplication sharedApplication] keyWindow];
    if (window.windowLevel != UIWindowLevelNormal)
    {
        NSArray *windows = [[UIApplication sharedApplication] windows];
        for(UIWindow * tmpWin in windows)
        {
            if (tmpWin.windowLevel == UIWindowLevelNormal)
            {
                window = tmpWin;
                break;
            }
        }
    }
    // 获取window的rootViewController
    result = window.rootViewController;
    
    while (result.presentedViewController) {
        result = result.presentedViewController;
    }
    if ([result isKindOfClass:[UITabBarController class]]) {
        result = [(UITabBarController *)result selectedViewController];
    }
    if ([result isKindOfClass:[UINavigationController class]]) {
        result = [(UINavigationController *)result visibleViewController];
    }
    return result;
}

@end
