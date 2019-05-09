//
//  RunTrace.m
//  RunTrace
//
//  Created by 孙昕 on 15/9/18.
//  Copyright (c) 2015年 孙昕. All rights reserved.
//

#import "RunTrace.h"
#import <objc/runtime.h>
#import "RunTraceHelp.h"
@interface RunTrace ()
{
    BOOL bTouch;
    CGFloat left,top;
    __weak UIView *viewTouch;
    UIView *viewBound;
    UIWindow *winInfo;
    UILabel *lbInfo;
    RunTraceHelp *viewTraceHelp;
    NSMutableArray *arrViewHit;
    __weak UIView *viewMain;
    BOOL bUnload;
    __weak UIView *viewLeak;
    NSString *strLeak;
}
+(void)hookMethod:(Class)cls OriginSelector:(SEL)originalSelector SwizzledSelector:(SEL)swizzledSelector;
@end
#if RunTraceOpen
@implementation UIWindow (Load)
//+(void)load
//{
//    [RunTrace hookMethod:[UIWindow class] OriginSelector:@selector(makeKeyAndVisible) SwizzledSelector:@selector(myMakeKeyAndVisible)];
//}

//-(void)myMakeKeyAndVisible
//{
////    [self myMakeKeyAndVisible];
////    if(self.frame.size.height>20 && self.tag == 9999)
////    {
////        if (![self viewWithTag:9998]) {
//            RunTrace *view=[[RunTrace alloc] init];
////            view.tag = 9998;
//            [self addSubview:view];
////        }
//
////    }
//}

@end

@implementation UIView(Remove)
+(void)load
{
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        [RunTrace hookMethod:[UIView class] OriginSelector:@selector(willMoveToSuperview:) SwizzledSelector:@selector(myWillMoveToSuperview:)];
        [RunTrace hookMethod:[UIView class] OriginSelector:@selector(willRemoveSubview:) SwizzledSelector:@selector(myWillRemoveSubview:)];
        [RunTrace hookMethod:[UIView class] OriginSelector:@selector(didAddSubview:) SwizzledSelector:@selector(myDidAddSubview:)];
        
    });
}


-(void)myDidAddSubview:(UIView *)subview
{
    [self myDidAddSubview:subview];
    if(subview!=nil)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceAddSubView object:self userInfo:@{@"subview":subview}];
    }
}

-(void)myWillRemoveSubview:(UIView *)subview
{
    [self myWillRemoveSubview:subview];
    if(subview!=nil)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceRemoveSubView object:self userInfo:@{@"subview":subview}];
    }
}

-(void)myWillMoveToSuperview:(UIView*)newSuperview
{
    [self myWillMoveToSuperview:newSuperview];
    if(newSuperview==nil)
    {
        [[NSNotificationCenter defaultCenter] postNotificationName:msgRunTraceRemoveView object:self];
    }
}
@end
#endif
@implementation RunTrace
+ (void)hookMethod:(Class)cls OriginSelector:(SEL)originalSelector SwizzledSelector:(SEL)swizzledSelector
{
        Method originalMethod = class_getInstanceMethod(cls, originalSelector);
        Method swizzledMethod = class_getInstanceMethod(cls, swizzledSelector);
        
        BOOL didAddMethod =
        class_addMethod(cls,
                        originalSelector,
                        method_getImplementation(swizzledMethod),
                        method_getTypeEncoding(swizzledMethod));
        
        if (didAddMethod) {
            class_replaceMethod(cls,
                                swizzledSelector,
                                method_getImplementation(originalMethod),
                                method_getTypeEncoding(originalMethod));
        } else {
            method_exchangeImplementations(originalMethod, swizzledMethod);
        }
}

- (void)show {
//    [self.window addSubview:viewBound];

    viewBound.hidden = NO;
    self.hidden = NO;
}

- (void)hide {
    [viewBound removeFromSuperview];
    viewBound.hidden = YES;
    [winInfo removeFromSuperview];
    winInfo.hidden = YES;
    [viewTraceHelp onClose:nil];
    self.hidden = YES;
}

-(instancetype)init
{
    if(self=[super init])
    {
        self.frame=CGRectMake([UIScreen mainScreen].bounds.size.width-35, 100, 30, 30) ;
        self.layer.zPosition=FLT_MAX;
        UILabel *lb=[[UILabel alloc] initWithFrame:self.bounds];
        lb.autoresizingMask=UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleHeight;
        lb.textAlignment=NSTextAlignmentCenter;
        lb.text=@"T";
        lb.backgroundColor=[UIColor colorWithRed:0.253 green:0.917 blue:0.476 alpha:1.000];
        [self addSubview:lb];
        self.layer.masksToBounds=YES;
        self.layer.cornerRadius=15;
        bTouch=NO;
        arrViewHit=[[NSMutableArray alloc] initWithCapacity:30];
        viewBound=[[UIView alloc] init];
        viewBound.layer.masksToBounds=YES;
        viewBound.layer.borderWidth=3;
        viewBound.layer.borderColor=[UIColor blackColor].CGColor;
        viewBound.layer.zPosition=FLT_MAX;
        winInfo=[[UIWindow alloc] initWithFrame:CGRectMake(0, RUN_IPhoneX_NavBar_FixHeight, [UIScreen mainScreen].bounds.size.width, 50)];
        winInfo.backgroundColor=[UIColor colorWithRed:0.000 green:0.898 blue:0.836 alpha:0.7];
        winInfo.hidden=YES;
        winInfo.windowLevel=UIWindowLevelAlert;
        lbInfo=[[UILabel alloc] initWithFrame:winInfo.bounds];
        lbInfo.numberOfLines=2;
        lbInfo.backgroundColor=[UIColor clearColor];
        lbInfo.lineBreakMode=NSLineBreakByCharWrapping;
        lbInfo.autoresizingMask=UIViewAutoresizingFlexibleWidth|UIViewAutoresizingFlexibleWidth;
        UITapGestureRecognizer *tap=[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapInfo)];
        lbInfo.userInteractionEnabled=YES;
        [lbInfo addGestureRecognizer:tap];
        [winInfo addSubview:lbInfo];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTraceSuperView:) name:msgRunTraceSuperView object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTraceSubView:) name:msgRunTraceSubView object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTraceContraints:) name:msgRunTraceContraints object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTraceInfoPosition:) name:msgRunTraceInfoPosition object:nil];
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(handleTraceAddSubView:) name:msgRunTraceAddSubView object:nil];
        
        winInfo.alpha = 0;
    }
    return self;
}

-(void)dealloc
{
    [[NSNotificationCenter defaultCenter] removeObserver:self];
}

-(void)handleTraceAddSubView:(NSNotification*)nofi
{
    UIView *viewSuper=nofi.object;
    UIView *view=nofi.userInfo[@"subview"];
    if([viewSuper isKindOfClass:[UIWindow class]] && view!=self && ![view isKindOfClass:[RunTraceHelp class]])
    {
        [viewSuper bringSubviewToFront:self];
    }
}

-(void)handleTraceInfoPosition:(NSNotification*)nofi
{
    NSValue *value=nofi.object;
    CGRect frame=[value CGRectValue];
    winInfo.frame=frame;
}

-(void)handleTraceContraints:(NSNotification*)nofi
{
    NSDictionary *dic=nofi.object;
    UIView *view=((RunTraceObject*)dic[@"View"]).object;
    [self.window addSubview:viewBound];
    CGRect p=[self.window convertRect:view.bounds fromView:view];
    viewBound.frame=p;
    [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionAllowUserInteraction|UIViewAnimationOptionRepeat|UIViewAnimationOptionAutoreverse animations:^{
        [UIView setAnimationRepeatCount:2];
        viewBound.alpha=0;
    } completion:^(BOOL finished) {
        [viewBound removeFromSuperview];
        viewBound.alpha=1;
    }];
    UILabel *lbLine=[[UILabel alloc] initWithFrame:CGRectZero];
    lbLine.backgroundColor=[UIColor blueColor];
    NSString *strType=dic[@"Type"];
    CGFloat constant=[dic[@"Constant"] floatValue];
    UIView *viewTo=((RunTraceObject*)dic[@"ToView"]).object;
    if(constant!=0)
    {
        if([strType isEqualToString:@"Left"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x-constant, view.frame.origin.y+view.frame.size.height/2-2, constant, 4);
        }
        else if([strType isEqualToString:@"Right"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width, view.frame.origin.y+view.frame.size.height/2-2, constant, 4);
        }
        else if([strType isEqualToString:@"Top"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width/2-2, view.frame.origin.y-constant, 4, constant);
        }
        else if([strType isEqualToString:@"Bottom"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width/2-2, view.frame.origin.y+view.frame.size.height, 4, constant);
        }
        else if([strType isEqualToString:@"Width"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x,view.frame.origin.y+view.frame.size.height/2-2, view.frame.size.width, 4);
        }
        else if([strType isEqualToString:@"Height"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width/2-2, view.frame.origin.y, 4, view.frame.size.height);
        }
        else if([strType isEqualToString:@"CenterX"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width/2-constant, view.frame.origin.y+view.frame.size.height/2-2, constant, 4);
        }
        else if([strType isEqualToString:@"CenterY"])
        {
            lbLine.frame=CGRectMake(view.frame.origin.x+view.frame.size.width/2-2, view.frame.origin.y+view.frame.size.height/2-constant, 4, constant);
        }
        [self.window addSubview:lbLine];
        CGRect p=[self.window convertRect:lbLine.frame fromView:view.superview];
        lbLine.frame=p;
        [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionAllowUserInteraction|UIViewAnimationOptionRepeat|UIViewAnimationOptionAutoreverse animations:^{
            [UIView setAnimationRepeatCount:2];
            lbLine.alpha=0;
        } completion:^(BOOL finished) {
            [lbLine removeFromSuperview];
        }];
        
    }
    if(viewTo)
    {
        UIView* viewToBound=[[UIView alloc] init];
        viewToBound.layer.masksToBounds=YES;
        viewToBound.layer.borderWidth=3;
        viewToBound.layer.borderColor=[UIColor redColor].CGColor;
        viewToBound.layer.zPosition=FLT_MAX;
        [self.window addSubview:viewToBound];
        CGRect p=[self.window convertRect:viewTo.bounds fromView:viewTo];
        viewToBound.frame=p;
        [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionAllowUserInteraction|UIViewAnimationOptionRepeat|UIViewAnimationOptionAutoreverse animations:^{
            [UIView setAnimationRepeatCount:2];
            viewToBound.alpha=0;
        } completion:^(BOOL finished) {
            [viewToBound removeFromSuperview];
        }];
    }
}

-(void)handleTraceSuperView:(NSNotification*)nofi
{
    UIView *view=nofi.object;
    [self.window addSubview:viewBound];
    CGRect p=[self.window convertRect:view.bounds fromView:view];
    viewBound.frame=p;
    [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionAllowUserInteraction|UIViewAnimationOptionRepeat|UIViewAnimationOptionAutoreverse animations:^{
        [UIView setAnimationRepeatCount:2];
        viewBound.alpha=0;
    } completion:^(BOOL finished) {
        [viewBound removeFromSuperview];
        viewBound.alpha=1;
    }];
    
}

-(void)handleTraceSubView:(NSNotification*)nofi
{
    UIView *view=nofi.object;
    [self.window addSubview:viewBound];
    CGRect p=[self.window convertRect:view.bounds fromView:view];
    viewBound.frame=p;
    [UIView animateWithDuration:0.3 delay:0 options:UIViewAnimationOptionAllowUserInteraction|UIViewAnimationOptionRepeat|UIViewAnimationOptionAutoreverse animations:^{
        [UIView setAnimationRepeatCount:2];
        viewBound.alpha=0;
    } completion:^(BOOL finished) {
        [viewBound removeFromSuperview];
        viewBound.alpha=1;
    }];
}

-(void)tapInfo
{
    if(viewTraceHelp.superview)
    {
        return;
    }
    viewTraceHelp=[[[NSBundle mainBundle] loadNibNamed:@"RunTraceHelp" owner:nil options:nil] lastObject];
    viewTraceHelp.layer.zPosition=FLT_MAX;
    viewTraceHelp.viewHit=viewTouch;
    if(winInfo.frame.origin.y>0)
    {
        [viewTraceHelp.btnPosition setTitle:@"Bottom" forState:UIControlStateNormal];
    }
    [self.window addSubview:viewTraceHelp];
    viewTraceHelp.center=self.window.center;
}

-(void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=YES;
    winInfo.alpha=1;
    winInfo.hidden=NO;
    [viewTraceHelp removeFromSuperview];
    [viewBound removeFromSuperview];
    UITouch *touch=[touches anyObject];
    CGPoint point=[touch locationInView:self];
    left=point.x;
    top=point.y;
    CGPoint topPoint=[touch locationInView:self.window];
    UIView *view=[self topView:self.window Point:topPoint];
    CGRect frame=[self.window convertRect:view.bounds fromView:view];
    viewTouch=view;
    viewBound.frame=frame;
    [self.window addSubview:viewBound];
    lbInfo.text=[NSString stringWithFormat:@"  %@ l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf",NSStringFromClass([view class]),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height];
    
}

-(void)touchesMoved:(NSSet *)touches withEvent:(UIEvent *)event
{
    if(!bTouch)
    {
        return;
    }
    UITouch *touch=[touches anyObject];
    CGPoint point=[touch locationInView:self.window];
    self.frame=CGRectMake(point.x-left, point.y-top, self.frame.size.width, self.frame.size.height);
    CGPoint topPoint=[touch locationInView:self.window];
    UIView *view=[self topView:self.window Point:topPoint];
    CGRect frame=[self.window convertRect:view.bounds fromView:view];
    viewTouch=view;
    viewBound.frame=frame;
    lbInfo.text=[NSString stringWithFormat:@"  %@ l:%0.1lf t:%0.1lf w:%0.1lf h:%0.1lf",NSStringFromClass([view class]),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height];
    winInfo.alpha=1;
    winInfo.hidden=NO;
}

-(void)touchesCancelled:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=NO;
    [viewBound removeFromSuperview];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [UIView animateWithDuration:0.5 animations:^{
            winInfo.alpha=0;
        } completion:^(BOOL finished) {
            winInfo.hidden=YES;
        }];
    });
}

-(void)touchesEnded:(NSSet *)touches withEvent:(UIEvent *)event
{
    bTouch=NO;
    [viewBound removeFromSuperview];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [UIView animateWithDuration:0.5 animations:^{
            winInfo.alpha=0;
        } completion:^(BOOL finished) {
            winInfo.hidden=YES;
        }];
    });
    
}

- (UIViewController*)topViewController {
    UIViewController *vc=nil;
    if([[[UIApplication sharedApplication] delegate] window] .rootViewController!=nil)
    {
        vc=[[[UIApplication sharedApplication] delegate] window] .rootViewController;
    }
    else if([[[UIApplication sharedApplication] delegate] window].rootViewController!=nil)
    {
        vc=[[[UIApplication sharedApplication] delegate] window].rootViewController;
    }
    return [self topViewControllerWithRootViewController:vc];
}

-(UIViewController*)topViewControllerWithRootViewController:(UIViewController*)rootViewController {
    if ([rootViewController isKindOfClass:[UITabBarController class]]) {
        UITabBarController* tabBarController = (UITabBarController*)rootViewController;
        return [self topViewControllerWithRootViewController:tabBarController.selectedViewController];
    } else if ([rootViewController isKindOfClass:[UINavigationController class]]) {
        UINavigationController* navigationController = (UINavigationController*)rootViewController;
        return [self topViewControllerWithRootViewController:navigationController.visibleViewController];
    } else if (rootViewController.presentedViewController) {
        UIViewController* presentedViewController = rootViewController.presentedViewController;
        return [self topViewControllerWithRootViewController:presentedViewController];
    } else {
        return rootViewController;
    }
}

-(void)hitTest:(UIView*)view Point:(CGPoint) point;
{
    if([view isKindOfClass:[UIScrollView class]])
    {
        point.x+=((UIScrollView*)view).contentOffset.x;
        point.y+=((UIScrollView*)view).contentOffset.y;
    }
    if ([view pointInside:point withEvent:nil] &&
        (!view.hidden) &&
        (view.alpha >= 0.01f) && (view!=viewBound) && ![view isDescendantOfView:self]) {
        [arrViewHit addObject:view];
        for (UIView *subView in view.subviews) {
            CGPoint subPoint = CGPointMake(point.x - subView.frame.origin.x,
                                           point.y - subView.frame.origin.y);
            [self hitTest:subView Point:subPoint];
        }
    }
}

-(UIView*)topView:(UIView*)view Point:(CGPoint) point;
{
    [arrViewHit removeAllObjects];
    [self hitTest:view Point:point];
    UIView *viewTop=[arrViewHit lastObject];
    [arrViewHit removeAllObjects];
    return viewTop;
}
@end










