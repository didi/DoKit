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
        self.borderColor = [UIColor redColor];
        self.borderWidth = 1;
        self.enable = NO;
        self.ignoreSystemView = YES;
        self.blackList = @[NSStringFromClass([UIVisualEffectView class])];
    }
    return self;
}

- (void)setEnable:(BOOL)enable
{
    _enable = enable;
    
    for (UIWindow *window in [UIApplication sharedApplication].windows) {
        if (enable) {
            [window showDoraemonMetricsRecursive];
        } else {
            [window hideDoraemonMetricsRecursive];
        }
    }
}

@end
