//
//  DoraemonHierarchyWindow.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyWindow.h"
#import "DoraemonHierarchyViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyWindow ()

@end

@implementation DoraemonHierarchyWindow

- (instancetype)init {
    return [self initWithFrame:[UIScreen mainScreen].bounds];
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        self.windowLevel = UIWindowLevelAlert - 1;
        if (!self.rootViewController) {
            self.rootViewController = [[DoraemonHierarchyViewController alloc] init];
        }
    }
    return self;
}

- (void)show {
    [self makeKeyAndVisible];
//    self.hidden = NO;
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonShowPluginNotification object:nil userInfo:nil];
}

- (void)hide {
    [self resignKeyWindow];
    self.hidden = YES;
}

@end
