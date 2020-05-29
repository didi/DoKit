//
//  DoraemonUIProfileManager.m
//  DoraemonKit
//
//  Created by xgb on 2019/8/1.
//

#import "DoraemonUIProfileManager.h"
#import "UIViewController+DoraemonUIProfile.h"
#import "DoraemonUIProfileWindow.h"
#import "UIViewController+Doraemon.h"

@interface DoraemonUIProfileManager ()

@end

@implementation DoraemonUIProfileManager

+ (instancetype)sharedInstance
{
    static DoraemonUIProfileManager *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [DoraemonUIProfileManager new];
    });
    
    return sharedInstance;
}

- (void)setEnable:(BOOL)enable
{
    _enable = enable;
    if (enable) {
        [[UIViewController topViewControllerForKeyWindow] profileViewDepth];
    } else {
        [[UIViewController topViewControllerForKeyWindow] resetProfileData];
        [[DoraemonUIProfileWindow sharedInstance] hide];
    }
}

@end
