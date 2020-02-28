//
//  DoraemonViewMetricsManager.m
//  DoraemonKit
//
//  Created by xgb on 2018/12/11.
//

#import "DoraemonViewMetricsConfig.h"
#import "UIView+DoraemonViewMetrics.h"

@implementation DoraemonViewMetricsConfig

+ (instancetype)defaultConfig
{
    static DoraemonViewMetricsConfig *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [DoraemonViewMetricsConfig new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.borderWidth = 1;
        self.enable = NO;
    }
    return self;
}

- (void)setEnable:(BOOL)enable
{
    _enable = enable;
    
    for (UIWindow *window in [UIApplication sharedApplication].windows) {
        [window doraemonMetricsRecursiveEnable:enable];
    }
}

@end
