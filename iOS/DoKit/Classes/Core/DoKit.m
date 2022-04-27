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

API_AVAILABLE(ios(13.0))
static NSArray<UIWindowScene *> *_Nullable getAllForgroundApplicationWindowScene(void) {
    __block NSMutableArray<UIWindowScene *> *windowSceneArray = nil;
    [UIApplication.sharedApplication.connectedScenes enumerateObjectsUsingBlock:^(UIScene *_Nonnull obj, BOOL *_Nonnull __attribute__((unused)) stop) {
        if ([obj.session.role isEqualToString:UIWindowSceneSessionRoleApplication] && [obj isKindOfClass:UIWindowScene.class]) {
//            *stop = YES;
            if (!windowSceneArray) {
                windowSceneArray = NSMutableArray.array;
            }
            [windowSceneArray addObject:(UIWindowScene *) obj];
        }
    }];
    
    return windowSceneArray.copy;
}

static NSSet<UIWindow *> *_Nullable windowSet = nil;

NS_ASSUME_NONNULL_END

@implementation DoKit

+ (void)installWithProductId:(NSString *)productId {
    UIWindow *trayWindow = nil;
    CGRect trayWindowFrame;
    if (@available(iOS 13.0, *)) {
        UIWindowScene *windowScene = getAllForgroundApplicationWindowScene().firstObject;
        if (!windowScene) {
#ifndef NS_BLOCK_ASSERTIONS
            NSAssert(NO, @"UIWindowScene which is foreground and application type is not founded.");
#endif

            return;
        }
        trayWindow = [[UIWindow alloc] initWithWindowScene:windowScene];
        trayWindowFrame = CGRectMake(0, windowScene.screen.bounds.size.height / 3, 58, 58);
    } else {
        trayWindowFrame = CGRectMake(0, UIScreen.mainScreen.bounds.size.height / 3, 58, 58);
    }
    if (trayWindow != nil) {
        trayWindow.frame = trayWindowFrame;
    } else {
        trayWindow = [[UIWindow alloc] initWithFrame:trayWindowFrame];
    }
    NSURL *resourceBundleUrl = [[NSBundle bundleForClass:self.class] URLForResource:@"DoKitResource" withExtension:@"bundle"];
    if (!resourceBundleUrl) {
#ifndef NS_BLOCK_ASSERTIONS
        NSAssert(NO, @"DoKitResource.bundle not found.");
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

@end
