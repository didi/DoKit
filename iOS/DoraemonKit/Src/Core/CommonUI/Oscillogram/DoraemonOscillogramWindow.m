//
//  DoraemonOscillogramWindow.m
//  CocoaLumberjack
//
//  Created by yixiang on 2018/1/3.
//

#import "DoraemonOscillogramWindow.h"
#import "DoraemonOscillogramViewController.h"
#import "UIColor+Doraemon.h"
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
        self.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.33];
        
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
    if (point.x>DoraemonScreenWidth-kDoraemonSizeFrom750(60) && point.y<kDoraemonSizeFrom750(60)+IPHONE_TOPSENSOR_HEIGHT) {
        return [super pointInside:point withEvent:event];
    }
    return NO;
}

- (void)show{
    self.hidden = NO;
    self.frame = CGRectMake(0, 0, DoraemonScreenWidth, kDoraemonSizeFrom750(480)+IPHONE_TOPSENSOR_HEIGHT);
    [_vc startRecord];
}

- (void)hide{
    [_vc endRecord];
    self.hidden = YES;
    
}

@end
