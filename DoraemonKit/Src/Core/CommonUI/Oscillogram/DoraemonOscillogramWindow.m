//
//  DoraemonOscillogramWindow.m
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonOscillogramWindow.h"
#import "DoraemonOscillogramViewController.h"
#import "UIColor+DoreamonKit.h"
#import "DoraemonDefine.h"

@interface DoraemonOscillogramWindow()

@end

@implementation DoraemonOscillogramWindow

+ (DoraemonOscillogramWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonOscillogramWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonOscillogramWindow alloc] initWithFrame:CGRectZero];
    });
    return instance;
}

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.windowLevel = UIWindowLevelStatusBar + 100.f;
        self.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.3];
        
        [self addRootVc];
    }
    return self;
}

- (void)addRootVc{
   //需要子类重写
}

- (void)becomeKeyWindow{
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

- (BOOL)pointInside:(CGPoint)point withEvent:(UIEvent *)event{
    return NO;
}

- (void)show{
    self.hidden = NO;
    self.frame = CGRectMake(0, 0, DoraemonScreenWidth, 240);
    [_vc startRecord];
}

- (void)hide{
    [_vc endRecord];
    self.hidden = YES;
    
}

@end
