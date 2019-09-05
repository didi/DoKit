//
//  UIViewController+Doraemon.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/5.
//

#import "UIViewController+Doraemon.h"

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

@end
