//
//  DoraemonVisualMagnifierWindow.m
//  DoraemonKit
//
//  Created by wenquan on 2018/12/6.
//

#import "DoraemonVisualMagnifierWindow.h"

@implementation DoraemonVisualMagnifierWindow

#pragma mark - Lifecycle

- (void)dealloc {
    NSLog(@"DoraemonVisualMagnifierWindow dealloc");
}

-(instancetype)init{
    self = [super init];
    if (self) {
        
        self.magnifierWidth = 90;
        self.magnification = 1.5;
        self.adjustPoint = CGPointMake(0, 0);
        
        self.frame = CGRectMake(0, 0, _magnifierWidth, _magnifierWidth);
        self.layer.borderWidth = 1;
//        self.layer.borderColor = [[[UIColor lightGrayColor] colorWithAlphaComponent:0.9] CGColor];
        self.layer.borderColor = [[UIColor clearColor] CGColor];
        //为了居于状态条之上
        self.windowLevel = UIWindowLevelStatusBar + 1;
        //        self.windowLevel = UIWindowLevelAlert;
        self.layer.delegate = self;
        //保证和屏幕读取像素的比例一致
        self.layer.contentsScale = [[UIScreen mainScreen] scale];
    }
    return self;
}

#pragma mark - Override

- (void)drawLayer:(CALayer *)layer inContext:(CGContextRef)ctx {
    //提前位移半个长宽的坑
    CGContextTranslateCTM(ctx, self.frame.size.width * 0.5, self.frame.size.height * 0.5);
    CGContextScaleCTM(ctx, _magnification, _magnification);
    //再次位移后就可以把触摸点移至self.center的位置
    CGContextTranslateCTM(ctx, -1 * self.targetPoint.x, -1 * self.targetPoint.y);
    
    [self.targetWindow.layer renderInContext:ctx];
}

- (void)setHidden:(BOOL)hidden {
    [super setHidden:hidden];
//    self.layer.borderColor = hidden ? [[UIColor clearColor] CGColor] : [[UIColor lightGrayColor] CGColor];
    self.layer.borderColor = [[UIColor clearColor] CGColor];
}

#pragma mark - Setter

- (void)setAdjustPoint:(CGPoint)adjustPoint {
    _adjustPoint = adjustPoint;
    [self setTargetPoint:self.targetPoint];
}

- (void)setMagnification:(float)magnification{
    if(magnification<=1) return;
    _magnification = magnification;
}

- (void)setTargetWindow:(UIView *)targetWindow {
    _targetWindow = targetWindow;
//    [self makeKeyAndVisible];
    [self setTargetPoint:self.targetPoint];
    
}
- (void)setMagnifierWidth:(float)magnifierWidth {
    _magnifierWidth = magnifierWidth;
    self.frame = CGRectMake(0, 0, _magnifierWidth, _magnifierWidth);
    self.layer.cornerRadius = self.magnifierWidth/2;
    self.layer.masksToBounds = YES;
}

- (void)setTargetPoint:(CGPoint)targetPoint {
    _targetPoint = targetPoint;
    if (self.targetWindow) {
//        CGPoint center = CGPointMake(targetPoint.x, self.center.y);
//        if (targetPoint.y > CGRectGetHeight(self.bounds) * 0.5) {
//            center.y = targetPoint.y -  CGRectGetHeight(self.bounds) / 2;
//        }
        CGPoint center = CGPointMake(targetPoint.x, targetPoint.y);
        self.center = CGPointMake(center.x + self.adjustPoint.x, center.y + self.adjustPoint.y);
        [self.layer setNeedsDisplay];
    }
}

@end
