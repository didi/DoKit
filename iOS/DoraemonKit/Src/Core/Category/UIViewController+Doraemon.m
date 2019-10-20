//
//  UIViewController+Doraemon.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/5.
//

#import "UIViewController+Doraemon.h"
#import "UIView+Doraemon.h"

@implementation UIViewController (Doraemon)

- (UIEdgeInsets)safeAreaInset:(UIView *)view {
    if (@available(iOS 11.0, *)) {
        return view.safeAreaInsets;
    }
    return UIEdgeInsetsZero;
}

// safe area inset
- (UIEdgeInsets)safeAreaInset {
    return [self safeAreaInset:self.view];
}

// 默认采用view frame | 调整刘海屏 | 支持转向调整
- (CGRect) fullscreen {
    CGRect screen = self.view.frame;
    UIInterfaceOrientation orientation = [UIApplication sharedApplication].statusBarOrientation;
    switch (orientation) {
        case UIInterfaceOrientationLandscapeLeft:
        case UIInterfaceOrientationLandscapeRight:
        {
            CGSize size = self.view.doraemon_size;
            if (size.width > size.height) {
                UIEdgeInsets safeAreaInsets = [self safeAreaInset];
                CGRect frame = screen;
                CGFloat width = self.view.doraemon_width - safeAreaInsets.left - safeAreaInsets.right;
                frame.origin.x = safeAreaInsets.left;
                frame.size.width = width;
                screen = frame;
            }
        }
            break;
        default:
            screen = screen;
            break;
    }
    
    return screen;
}

+ (UIViewController *)rootViewControllerForKeyWindow{
    return [[[UIApplication sharedApplication].delegate window] rootViewController];
}

+ (UIViewController *)topViewControllerForKeyWindow {
    UIViewController *resultVC;
    resultVC = [self _topViewController:[[[UIApplication sharedApplication].delegate window] rootViewController]];
    while (resultVC.presentedViewController) {
        resultVC = [self _topViewController:resultVC.presentedViewController];
    }
    return resultVC;
}

+ (UIViewController *)_topViewController:(UIViewController *)vc {
    if ([vc isKindOfClass:[UINavigationController class]]) {
        return [self _topViewController:[(UINavigationController *)vc topViewController]];
    } else if ([vc isKindOfClass:[UITabBarController class]]) {
        return [self _topViewController:[(UITabBarController *)vc selectedViewController]];
    } else {
        return vc;
    }
    return nil;
}

@end
