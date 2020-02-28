//
//  UIViewController+DoraemonHierarchy.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "UIViewController+DoraemonHierarchy.h"
#import "DoraemonDefine.h"

@implementation UIViewController (DoraemonHierarchy)

- (UIViewController *)doraemon_currentShowingViewController {
    
    UIViewController *vc = self;
    if ([self presentedViewController]) {
        vc = [[self presentedViewController] doraemon_currentShowingViewController];
    } else if ([vc isKindOfClass:[UITabBarController class]]) {
        UITabBarController *tabBar = (UITabBarController *)vc;
        vc = [tabBar.selectedViewController doraemon_currentShowingViewController];
    } else if ([vc isKindOfClass:[UINavigationController class]]) {
        UINavigationController *nav = (UINavigationController *)vc;
        vc = [[nav visibleViewController] doraemon_currentShowingViewController];
    }
    return vc;
}

- (void)doraemon_showAlertControllerWithMessage:(NSString *)message handler:(void (^)(NSInteger action))handler {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:@"Note" message:message preferredStyle:UIAlertControllerStyleAlert];
    UIAlertAction *cancel = [UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:^(UIAlertAction * action) {
        if (handler) {
            handler(0);
        }
    }];
    UIAlertAction *confirm = [UIAlertAction actionWithTitle:@"Confirm" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * action) {
        if (handler) {
            handler(1);
        }
    }];
    [alert addAction:cancel];
    [alert addAction:confirm];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self presentViewController:alert animated:YES completion:nil];
    });
}

- (void)doraemon_showActionSheetWithTitle:(NSString *)title actions:(NSArray *)actions currentAction:(NSString *)currentAction completion:(void (^)(NSInteger index))completion {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:title preferredStyle:UIAlertControllerStyleActionSheet];
    for (NSInteger i = 0; i < actions.count; i++) {
        NSString *actionTitle = actions[i];
        __block NSInteger index = i;
        UIAlertAction *action = [UIAlertAction actionWithTitle:actionTitle style:UIAlertActionStyleDefault handler:^(UIAlertAction * _Nonnull action) {
            if (completion) {
                completion(index);
            }
        }];
        if (currentAction && [actionTitle isEqualToString:currentAction]) {
            action.enabled = NO;
            [action setValue:[UIImage doraemon_imageNamed:@"doraemon_hierarchy_select"] forKey:@"image"];
        }
        [alert addAction:action];
    }
    [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self presentViewController:alert animated:YES completion:nil];
    });
}

- (void)doraemon_showTextFieldAlertControllerWithMessage:(NSString *)message text:(nullable NSString *)text handler:(nullable void (^)(NSString * _Nullable))handler {
    UIAlertController *alert = [UIAlertController alertControllerWithTitle:nil message:message preferredStyle:UIAlertControllerStyleAlert];
    [alert addTextFieldWithConfigurationHandler:^(UITextField * _Nonnull textField) {
        textField.text = text;
    }];
    [alert addAction:[UIAlertAction actionWithTitle:@"Cancel" style:UIAlertActionStyleCancel handler:nil]];
    [alert addAction:[UIAlertAction actionWithTitle:@"Confirm" style:UIAlertActionStyleDestructive handler:^(UIAlertAction * _Nonnull action) {
        if (handler) {
            handler(alert.textFields.firstObject.text);
        }
    }]];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self presentViewController:alert animated:YES completion:nil];
    });
}

@end
