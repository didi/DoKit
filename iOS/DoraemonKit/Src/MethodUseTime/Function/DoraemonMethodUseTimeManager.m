//
//  DoraemonMethodUseTimeManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/1/18.
//

#import "DoraemonMethodUseTimeManager.h"
#import "DoraemonCacheManager.h"

@implementation DoraemonMethodUseTimeManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (void)setOn:(BOOL)on{
    [[DoraemonCacheManager sharedInstance] saveMethodUseTimeSwitch:on];
}

- (BOOL)on{
    return [[DoraemonCacheManager sharedInstance] methodUseTimeSwitch];
}

@end
