/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "DKQRCodeScanViewController.h"
#import <DoraemonKit/DKQRCodeScanView.h>

NS_ASSUME_NONNULL_BEGIN

static inline void removeViewController(UIViewController *viewController, BOOL isAnimated);

@interface DKQRCodeScanViewController ()

@property(nonatomic, nullable, weak) DKQRCodeScanView *qrCodeScanView;

@end

NS_ASSUME_NONNULL_END

void removeViewController(UIViewController *viewController, BOOL isAnimated) {
#ifndef NS_BLOCK_ASSERTIONS
    NSCAssert(viewController.navigationController.viewControllers.count > 1, @"viewController.navigationController.viewControllers.count <= 1.");
#endif
    // No COW.
    NSMutableArray<__kindof UIViewController *> *viewControllerArray = viewController.navigationController.viewControllers.mutableCopy;
    [viewControllerArray removeObject:viewController];
    [viewController.navigationController setViewControllers:viewControllerArray.copy animated:isAnimated];
}

@implementation DKQRCodeScanViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    // Do any additional setup after loading the view.
    DKQRCodeScanView *qrCodeScanView = [[DKQRCodeScanView alloc] initWithFrame:self.view.bounds];
    [self.view addSubview:qrCodeScanView];
    self.qrCodeScanView = qrCodeScanView;
    __weak typeof(self) weakSelf = self;
    [qrCodeScanView startScanQRCodeWithCompletionBlock:^(DKQRCodeScanResult qrCodeScanResult, NSString *decodedString) {
        if (!weakSelf) {
            return;
        }
        typeof(weakSelf) self = weakSelf;
        void (^completionBlock)(NSString *_Nullable decodedString) = ^(NSString *_Nullable decodedString) {
            UIViewController *efficientViewContainer = nil;
            UIViewController *currentViewController = self;
            while (!efficientViewContainer) {
                if ([currentViewController.parentViewController isKindOfClass:UINavigationController.class] && currentViewController.navigationController.viewControllers.count > 1) {
                    efficientViewContainer = currentViewController.navigationController;
                } else if (currentViewController.parentViewController) {
                    currentViewController = currentViewController.parentViewController;
                } else if (currentViewController && currentViewController.presentingViewController.presentedViewController == currentViewController) {
                    efficientViewContainer = currentViewController.presentingViewController;
                } else {
#ifndef NS_BLOCK_ASSERTIONS
                    NSCAssert(NO, @"currentViewController hasn't container.");
#endif
                    break;
                }
            }
            if ([efficientViewContainer isKindOfClass:UINavigationController.class]) {
                removeViewController(currentViewController, YES);
            } else if (efficientViewContainer) {
                [currentViewController dismissViewControllerAnimated:YES completion:nil];
            }
            typeof(weakSelf) self = weakSelf;
            self.completionBlock ? self.completionBlock(decodedString) : (void) nil;
        };
        if (qrCodeScanResult == DKQRCodeScanResultAuthorityError) {
            UIAlertController *alertController = [UIAlertController alertControllerWithTitle:@"相机权限未开启，请到「设置-隐私-相机」中允许访问您的相机" message:nil preferredStyle:UIAlertControllerStyleAlert];
            UIAlertAction *openAlertAction = [UIAlertAction actionWithTitle:@"去开启" style:UIAlertActionStyleDefault handler:^(UIAlertAction *_Nonnull __attribute__((unused)) action) {
                typeof(weakSelf) self = weakSelf;
                completionBlock(nil);
                NSURL *settingUrl = [NSURL URLWithString:UIApplicationOpenSettingsURLString];
                if (@available(iOS 13.0, *)) {
                    [self.view.window.windowScene openURL:settingUrl options:nil completionHandler:nil];
                } else if (@available(iOS 10.0, *)) {
                    [UIApplication.sharedApplication openURL:settingUrl options:@{} completionHandler:nil];
                } else {
                    if ([UIApplication.sharedApplication canOpenURL:settingUrl]) {
                        [UIApplication.sharedApplication openURL:settingUrl];
                    }
                }
            }];
            UIAlertAction *cancelAlertAction = [UIAlertAction actionWithTitle:@"暂不开启" style:UIAlertActionStyleCancel handler:^(UIAlertAction *_Nonnull __attribute__((unused)) action) {
                completionBlock(nil);
            }];
            [alertController addAction:openAlertAction];
            [alertController addAction:cancelAlertAction];
            [self presentViewController:alertController animated:YES completion:nil];
        } else {
            completionBlock(decodedString);
        }
    }];
}

- (void)viewDidLayoutSubviews {
    [super viewDidLayoutSubviews];

    self.qrCodeScanView.frame = self.view.bounds;
}

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
