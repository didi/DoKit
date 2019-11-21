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
#import "DoraemonOscillogramWindowManager.h"

@interface DoraemonOscillogramWindow()

@property (nonatomic, strong) NSHashTable *delegates;

@end

@implementation DoraemonOscillogramWindow

- (NSHashTable *)delegates {
    if (_delegates == nil) {
        self.delegates = [NSHashTable weakObjectsHashTable];
    }
    return _delegates;
}

- (void)addDelegate:(id<DoraemonOscillogramWindowDelegate>) delegate {
    [self.delegates addObject:delegate];
}

- (void)removeDelegate:(id<DoraemonOscillogramWindowDelegate>)delegate {
    [self.delegates removeObject:delegate];
}

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
        self.windowLevel = UIWindowLevelStatusBar + 2.f;
        self.backgroundColor = [UIColor doraemon_colorWithHex:0x000000 andAlpha:0.33];
        self.layer.masksToBounds = YES;
        
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
    // 默认曲线图不拦截触摸事件，只有在关闭按钮z之类才响应
    if (CGRectContainsPoint(self.vc.closeBtn.frame, point)) {
        return [super pointInside:point withEvent:event];
    }
    return NO;
}

- (void)show{
    self.hidden = NO;
    [_vc startRecord];
    [self resetLayout];
}

- (void)hide{
    [_vc endRecord];
    self.hidden = YES;
    [self resetLayout];
    
    for (id<DoraemonOscillogramWindowDelegate> delegate in self.delegates) {
        [delegate doraemonOscillogramWindowClosed];
    }
}

- (void)resetLayout{
    [[DoraemonOscillogramWindowManager shareInstance] resetLayout];
}

@end
