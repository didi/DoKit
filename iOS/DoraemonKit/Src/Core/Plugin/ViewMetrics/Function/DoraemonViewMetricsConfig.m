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
    
    // 每当状态栏发生变化（如: 时间跳动，4G切WIFI，横竖屏切换等），状态栏会走`layoutSubviews`方法，会导致状态栏也出现元素边框；而[UIApplication sharedApplication].windows数组内拿不到UIStatusBarWindow，关闭元素边框无法及时隐藏状态栏的边框线
    // 也可以在`UIView+DoraemonViewMetrics.h`内的`shouldShowMetricsView`中直接禁掉状态栏的元素边框
    NSString *statusBarString = [NSString stringWithFormat:@"_statusBarWindow"];
    UIWindow *statusBarWindow = [[UIApplication sharedApplication] valueForKey:statusBarString];
    if (statusBarWindow) {
        if (!enable) {
            [statusBarWindow hideDoraemonMetricsRecursive];
        }
    }
}

@end
