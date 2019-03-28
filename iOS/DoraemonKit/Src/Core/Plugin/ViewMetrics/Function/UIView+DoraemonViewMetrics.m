//
//  UIView+DoraemonViewMetrics.m
//  DoraemonKit
//
//  Created by xgb on 2018/12/11.
//

#import "UIView+DoraemonViewMetrics.h"
#import "DoraemonMetricsView.h"
#import "DoraemonViewMetricsConfig.h"
#import "NSObject+Doraemon.h"

@implementation UIView (DoraemonViewMetrics)

+ (void)load{
    if ([NSStringFromClass([self class]) isEqualToString:@"UIView"]) {
        [[self  class] doraemon_swizzleInstanceMethodWithOriginSel:@selector(layoutSubviews) swizzledSel:@selector(doraemon_layoutSubviews)];
    }
}

- (void)doraemon_layoutSubviews
{
    [self doraemon_layoutSubviews];
    [self showDoraemonMetrics];
}

- (BOOL)shouldShowMetricsView
{
    if (![DoraemonViewMetricsConfig defaultConfig].enable) {
        return NO;
    }
    
    if ([self isKindOfClass:[DoraemonMetricsView class]]) {
        return NO;
    }
    
    //高德地图也有问题
    NSString *className = NSStringFromClass([self class]);
    if ([className hasPrefix:@"MA"]) {
        return NO;
    }
    
    // 状态栏不需要显示元素边框
    NSString *statusBarString = [NSString stringWithFormat:@"_statusBarWindow"];
    UIWindow *statusBarWindow = [[UIApplication sharedApplication] valueForKey:statusBarString];
    if (statusBarWindow && [self isDescendantOfView:statusBarWindow]) {
        return NO;
    }
    
    if ([self isInBlackList]) {
        return NO;
    }
    return YES;
}

- (void)showDoraemonMetricsRecursive
{
    for (UIView *subView in self.subviews) {
        [subView showDoraemonMetricsRecursive];
    }
    [self showDoraemonMetrics];
}

- (void)showDoraemonMetrics
{
    if (![self shouldShowMetricsView]) {
        [self hideDoraemonMetricsRecursive];
        return;
    }
    
    DoraemonMetricsView *metricsView = [self getDoraemonMetricsView];
    if (!metricsView) {
        metricsView = [[DoraemonMetricsView alloc] initWithFrame:self.bounds];
        metricsView.tag = [NSStringFromClass([DoraemonMetricsView class]) hash]+(NSInteger)self;
        metricsView.userInteractionEnabled = NO;
        [self addSubview:metricsView];
    }
    
    metricsView.layer.borderColor = [DoraemonViewMetricsConfig defaultConfig].borderColor.CGColor;
    metricsView.layer.borderWidth  = [DoraemonViewMetricsConfig defaultConfig].borderWidth;
    metricsView.hidden = ![DoraemonViewMetricsConfig defaultConfig].enable;
}

- (void)hideDoraemonMetricsRecursive
{
    for (UIView *subView in self.subviews) {
        [subView hideDoraemonMetricsRecursive];
    }
    [self hideDoraemonMetrics];
}

- (void)hideDoraemonMetrics
{
    DoraemonMetricsView *metricsView = [self getDoraemonMetricsView];
    if (metricsView) {
        metricsView.hidden = YES;
    }
}

- (DoraemonMetricsView *)getDoraemonMetricsView
{
    NSInteger tag = [NSStringFromClass([DoraemonMetricsView class]) hash]+(NSInteger)self;
    return (DoraemonMetricsView*)[self viewWithTag:tag];
}

- (BOOL)isInBlackList
{
    for (NSString *clsStr in [DoraemonViewMetricsConfig defaultConfig].blackList) {
        if ([self isKindOfClass:NSClassFromString(clsStr)]) {
            return YES;
        }
    }
    return NO;
}

@end
