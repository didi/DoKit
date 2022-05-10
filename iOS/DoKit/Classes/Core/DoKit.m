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

#import "DoKit.h"
#import <DoraemonKit/DKTrayViewController.h>

NS_ASSUME_NONNULL_BEGIN

static inline CGRect getInitialTrayWindowFrame(CGFloat screenHeight) {
    return CGRectMake(0, screenHeight / 3, 58, 58);
}

API_AVAILABLE(ios(13.0)) static inline UIWindow *createTrayWindow(UIWindowScene *_Nullable windowScene) {
    UIWindow *trayWindow = [[UIWindow alloc] initWithWindowScene:windowScene];
    trayWindow.frame = getInitialTrayWindowFrame(windowScene.screen.bounds.size.height);

    return trayWindow;
}

static inline UIWindow *createTrayWindowLegacy() {
    return [[UIWindow alloc] initWithFrame:getInitialTrayWindowFrame(UIScreen.mainScreen.bounds.size.height)];
}

static NSSet<UIWindow *> *_Nullable windowSet = nil;

@interface DoKit ()

+ (void)initWithTrayWindow:(UIWindow *)trayWindow;

@end

NS_ASSUME_NONNULL_END

@implementation DoKit

+ (void)initWithTrayWindow:(UIWindow *)trayWindow {
    NSURL *resourceBundleUrl = [[NSBundle bundleForClass:self.class] URLForResource:@"DoKitResource" withExtension:@"bundle"];
    if (!resourceBundleUrl) {
#ifndef NS_BLOCK_ASSERTIONS
        NSCAssert(NO, @"DoKitResource.bundle not found.");
#endif

        return;
    }
    DKTrayViewController *trayViewController = [[DKTrayViewController alloc] initWithNibName:@"DKTrayViewController" bundle:[NSBundle bundleWithURL:resourceBundleUrl]];
//    trayViewController.tapHandler = ^() {
//    };
    trayWindow.rootViewController = trayViewController;
    trayWindow.windowLevel = UIWindowLevelStatusBar + 1;
    trayWindow.hidden = NO;
    NSMutableSet *mutableWindowSet = windowSet.mutableCopy;
    windowSet = nil;
    if (!mutableWindowSet) {
        mutableWindowSet = NSMutableSet.set;
    }
    [mutableWindowSet addObject:trayWindow];
    windowSet = mutableWindowSet.copy;
}


+ (void)installWithWindowScene:(UIWindowScene *)windowScene productId:(nullable NSString *)productId {
    [self initWithTrayWindow:createTrayWindow(windowScene)];
}

+ (void)installWithProductId:(NSString *)productId {
    if (@available(iOS 13.0, *)) {
//        NSAssert(NO, @"Please use +[DoKit installWithWindowScene:productId: instead].");
        __block UIWindowScene *windowScene = nil;
        [UIApplication.sharedApplication.connectedScenes enumerateObjectsUsingBlock:^(UIScene *obj, BOOL *stop) {
            if ([obj.session.role isEqualToString:UIWindowSceneSessionRoleApplication] && [obj isKindOfClass:UIWindowScene.class]) {
                *stop = YES;
                windowScene = (UIWindowScene *) obj;
            }
        }];
        if (windowScene) {
            [self installWithWindowScene:windowScene productId:productId];
        }
    } else {
        [self initWithTrayWindow:createTrayWindowLegacy()];
    }
}

@end
