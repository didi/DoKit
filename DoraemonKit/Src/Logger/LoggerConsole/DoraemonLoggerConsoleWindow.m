//
//  DoraemonLoggerConsoleWindow.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleWindow.h"
#import "UIColor+DoraemonKit.h"
#import "DoraemonDefine.h"
#import "DoraemonLoggerConsoleViewController.h"

@interface DoraemonLoggerConsoleWindow()<DoraemonLoggerConsoleViewControllerDelegate>

@property (nonatomic, strong) DoraemonLoggerConsoleViewController *vc;

@end

@implementation DoraemonLoggerConsoleWindow

+ (DoraemonLoggerConsoleWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonLoggerConsoleWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonLoggerConsoleWindow alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.windowLevel = UIWindowLevelStatusBar + 100.f;
        self.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.7];
        
        DoraemonLoggerConsoleViewController *vc = [[DoraemonLoggerConsoleViewController alloc] init];
        vc.logger = [[DoraemonLogger alloc] init];
        vc.delegate = self;
        self.rootViewController = vc;
        _vc = vc;
        
    }
    return self;
}

//- (void)becomeKeyWindow{
//    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
//    [appWindow makeKeyWindow];
//}

- (void)show{
    self.hidden = NO;
    [self minimize];
}

- (void)hide{
    self.hidden = YES;
    [self minimize];
}

//最大化
- (void)maximize{
    self.frame = [UIScreen mainScreen].bounds;
    _vc.tipView.hidden = YES;
    _vc.searchView.hidden = NO;
    _vc.switchView.hidden = NO;
    _vc.tableView.hidden = NO;
    [self makeKeyWindow];
}

//最小化
- (void)minimize{
    self.frame = CGRectMake(0, 0, DoraemonScreenWidth, 20+IPHONE_TOPSENSOR_HEIGHT);
    _vc.tipView.hidden = NO;
    _vc.searchView.hidden = YES;
    _vc.switchView.hidden = YES;
    _vc.tableView.hidden = YES;
    
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

#pragma mark -- DoraemonLoggerConsoleViewControllerDelegate
- (void)toggleToMax{
    [self maximize];
}

- (void)toggleToMin{
    [self minimize];
}

@end
