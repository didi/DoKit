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
    self.hidden = NO;
}

- (void)hide {
    self.hidden = YES;
}

@end
