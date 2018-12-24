//
//  DoraemonVisualInfoWindow.m
//  DoraemonKit
//
//  Created by wenquan on 2018/12/5.
//

#import "DoraemonVisualInfoWindow.h"
#import "DoraemonDefine.h"

@implementation DoraemonVisualInfoWindow

#pragma mark - Lifecycle

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit {
    self.backgroundColor = [UIColor whiteColor];
    self.layer.cornerRadius = kDoraemonSizeFrom750(8);
    self.layer.borderWidth = 1.;
    self.layer.borderColor = [UIColor doraemon_colorWithHex:0x999999 andAlpha:0.2].CGColor;
    self.windowLevel = UIWindowLevelAlert;
    
    UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
    [self addGestureRecognizer:pan];
}

#pragma mark - Override

- (void)becomeKeyWindow {
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

#pragma mark - Actions

- (void)pan:(UIPanGestureRecognizer *)sender{
    UIView *panView = sender.view;
    
    if (!panView.hidden) {
        //1、获得拖动位移
        CGPoint offsetPoint = [sender translationInView:sender.view];
        //2、清空拖动位移
        [sender setTranslation:CGPointZero inView:sender.view];
        //3、重新设置控件位置
        CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
        CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
        
        CGPoint centerPoint = CGPointMake(newX, newY);
        panView.center = centerPoint;
    }
}

@end
