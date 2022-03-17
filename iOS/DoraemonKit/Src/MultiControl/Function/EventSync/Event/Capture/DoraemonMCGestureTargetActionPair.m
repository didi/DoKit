//
//  DoraemonMCGestureTargetActionPair.m
//  DoraemonKit
//
//  Created by litianhao on 2021/8/9.
//

#import "DoraemonMCGestureTargetActionPair.h"

@implementation DoraemonMCGestureTargetActionPair

- (instancetype)initWithTarget:(id)target action:(SEL)action sender:(id)sender {
    if (self = [super init]) {
        self.target = target;
        self.action = action;
        self.sender = sender;
    }
    return self;
}

- (BOOL)isEqualToTarget:(id)target andAction:(SEL)action {
    return (self.target == target) && [NSStringFromSelector(self.action) isEqualToString:NSStringFromSelector(action)];
}

- (BOOL)valid {
    return [self.target respondsToSelector:self.action];
}

- (void)doAction {
    if (![self valid]) {
        return;
    }
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Warc-performSelector-leaks"
    if ([NSStringFromSelector(self.action) containsString:@":"]) {
        [self.target performSelector:self.action withObject:self.sender];
    }else {
        [self.target performSelector:self.action];
    }
#pragma clang diagnostic pop
}


@end
