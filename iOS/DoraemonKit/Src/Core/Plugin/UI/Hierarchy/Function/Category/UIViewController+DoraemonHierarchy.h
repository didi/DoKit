//
//  UIViewController+DoraemonHierarchy.h
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface UIViewController (DoraemonHierarchy)

- (UIViewController *_Nullable)doraemon_currentShowingViewController;

- (void)doraemon_showAlertControllerWithMessage:(NSString *)message handler:(nullable void (^)(NSInteger action))handler;

- (void)doraemon_showActionSheetWithTitle:(NSString *)title actions:(NSArray *)actions currentAction:(nullable NSString *)currentAction completion:(nullable void (^)(NSInteger index))completion;

- (void)doraemon_showTextFieldAlertControllerWithMessage:(NSString *)message text:(nullable NSString *)text handler:(nullable void (^)(NSString * _Nullable))handler;

@end

NS_ASSUME_NONNULL_END
