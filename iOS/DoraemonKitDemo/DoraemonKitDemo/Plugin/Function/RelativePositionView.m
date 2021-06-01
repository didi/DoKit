//
//  RelativePositionView.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/26.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "RelativePositionView.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"
#import <objc/runtime.h>
#import "RelativePositionManager.h"


static CGFloat const kViewCheckSize = 62;

@interface RelativePositionView()

@property (nonatomic, strong) UIView *viewBound;//当前需要探测的view的边框

@property (nonatomic, assign) CGFloat left;
@property (nonatomic, assign) CGFloat top;
@property (nonatomic, strong) NSMutableArray *arrViewHit;
@property (nonatomic, strong) UIView *oldView;

@end

@implementation RelativePositionView


//初始化位置放在屏幕中间
-(instancetype)initWithLocation:(CGPoint)location{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(DoraemonScreenWidth/2-kViewCheckSize/2+location.x, DoraemonScreenHeight/2-kViewCheckSize/2, kViewCheckSize, kViewCheckSize);
        self.backgroundColor = [UIColor clearColor];
        self.layer.zPosition = FLT_MAX;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.bounds];
        imageView.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_visual"];
        [self addSubview:imageView];
        
        _arrViewHit = [[NSMutableArray alloc] initWithCapacity:30];
        
        _viewBound = [[UIView alloc] init];
        _viewBound.layer.masksToBounds = YES;
        _viewBound.layer.borderWidth = 2.;
        _viewBound.layer.borderColor = [UIColor doraemon_colorWithHex:0xCC3A4B].CGColor;
        _viewBound.layer.zPosition = FLT_MAX;
        
    }
     
    return self;
}

//转化为相对于屏幕的位置
-(CGRect)relativeFrameForScreenWithView:(UIView*)view{
    UIView* v=view;
    CGFloat x = .0;
    CGFloat y = .0;
    while (view != [UIApplication sharedApplication].keyWindow && nil != view) {
        x += view.frame.origin.x;
        y += view.frame.origin.y;
        view = view.superview;
        if ([view isKindOfClass:[UIScrollView class]]) {
            x -= ((UIScrollView *) view).contentOffset.x;
            y -= ((UIScrollView *) view).contentOffset.y;
        }
    }
    return CGRectMake(x, y, v.frame.size.width, v.frame.size.height);
}

// 四个函数相当于平移检测器pan,改变self.frame
- (void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    _left = point.x;
    _top = point.y;
    CGPoint topPoint = [touch locationInView:self.window];
    UIView *view = [self topView:self.window Point:topPoint];
    CGRect frame = [self.window convertRect:view.bounds fromView:view];
    _viewBound.frame = frame;
    [self.window addSubview:_viewBound];
    
    self.viewFrame = [self relativeFrameForScreenWithView:view];
    if ([self needRefresh:view]) {
        [RelativePositionManager.shareInstance refresh];
    }
}

-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self.window];
    self.frame = CGRectMake(point.x-_left, point.y-_top, self.frame.size.width, self.frame.size.height);
    
    CGPoint topPoint = [touch locationInView:self.window];
    UIView *view = [self topView:self.window Point:topPoint];
    CGRect frame = [self.window convertRect:view.bounds fromView:view];
    _viewBound.frame = frame;
    
    self.viewFrame = [self relativeFrameForScreenWithView:view];
    if ([self needRefresh:view]) {
        [RelativePositionManager.shareInstance refresh];
    }
}

-(void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
}

-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
}



-(UIView*)topView:(UIView*)view Point:(CGPoint) point{
    [_arrViewHit removeAllObjects];
    [self hitTest:view Point:point];
    UIView *viewTop=[_arrViewHit lastObject];
    [_arrViewHit removeAllObjects];
    return viewTop;
}


-(void)hitTest:(UIView*)view Point:(CGPoint) point{
    if([view isKindOfClass:[UIScrollView class]])
    {
        point.x+=((UIScrollView*)view).contentOffset.x;
        point.y+=((UIScrollView*)view).contentOffset.y;
    }
    if ([view pointInside:point withEvent:nil] &&
        (!view.hidden) &&
        (view.alpha >= 0.01f) && (view!=_viewBound) && ![view isDescendantOfView:self]) {
        [_arrViewHit addObject:view];
        for (UIView *subView in view.subviews) {
            CGPoint subPoint = CGPointMake(point.x - subView.frame.origin.x,
                                           point.y - subView.frame.origin.y);
            [self hitTest:subView Point:subPoint];
        }
    }
}

- (void)show {
    self.hidden = NO;
}

- (void)hide {
    [_viewBound removeFromSuperview];
    self.hidden = YES;
}

- (BOOL)needRefresh:(UIView *)view{
    if (!_oldView) {
        _oldView = view;
    }
    BOOL needRefresh = NO;
    if (_oldView != view) {
        needRefresh = YES;
        _oldView = view;
    }
    return needRefresh;
}

@end
