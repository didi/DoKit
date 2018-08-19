//
//  DoraemonViewCheckView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import "DoraemonViewCheckView.h"
#import "UIImage+DoraemonKit.h"
#import "DoraemonDefine.h"
#import "UIColor+DoreamonKit.h"

static CGFloat const kViewCheckSize = 40;

@interface DoraemonViewCheckView()

@property (nonatomic, strong) UIView *viewBound;//当前需要探测的view的边框
@property (nonatomic, strong) UIWindow *viewInfoWindow;//顶部被探测到的view的信息显示的UIwindow
@property (nonatomic, strong) UILabel *viewInfoLabel;//顶部被探测到的view的信息显示

@property (nonatomic, assign) CGFloat left;
@property (nonatomic, assign) CGFloat top;
@property (nonatomic, strong) NSMutableArray *arrViewHit;

@end

@implementation DoraemonViewCheckView

-(instancetype)init{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(DoraemonScreenWidth/2-kViewCheckSize/2, DoraemonScreenHeight/2-kViewCheckSize/2, kViewCheckSize, kViewCheckSize);
        self.backgroundColor = [UIColor clearColor];
        self.layer.zPosition = FLT_MAX;
        self.layer.cornerRadius = kViewCheckSize/2;
        self.layer.masksToBounds = YES;
        self.layer.borderColor = [UIColor orangeColor].CGColor;
        self.layer.borderWidth = 1;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.bounds];
        imageView.image = [UIImage doraemon_imageNamed:@"doraemon_finger"];
        [self addSubview:imageView];
        
        _arrViewHit = [[NSMutableArray alloc] initWithCapacity:30];
        
        _viewBound = [[UIView alloc] init];
        _viewBound.layer.masksToBounds = YES;
        _viewBound.layer.borderWidth = 3.;
        _viewBound.layer.borderColor = [UIColor blackColor].CGColor;
        _viewBound.layer.zPosition = FLT_MAX;
        
        
        _viewInfoWindow = [[UIWindow alloc] initWithFrame:CGRectMake(0, [UIScreen mainScreen].bounds.size.height-50, [UIScreen mainScreen].bounds.size.width, 50.)];
        _viewInfoWindow.backgroundColor = [UIColor colorWithRed:0.000 green:0.898 blue:0.836 alpha:0.7];
        _viewInfoWindow.windowLevel = UIWindowLevelAlert;
        [_viewInfoWindow makeKeyAndVisible];
        _viewInfoWindow.hidden = YES;
        
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.window makeKeyAndVisible];//将keyWindow还是交给原来那个
        });
        
        _viewInfoLabel = [[UILabel alloc] initWithFrame:_viewInfoWindow.bounds];
        _viewInfoLabel.numberOfLines = 0;
        _viewInfoLabel.backgroundColor =[UIColor clearColor];
        _viewInfoLabel.font = [UIFont systemFontOfSize:10];
        [_viewInfoWindow addSubview:_viewInfoLabel];
    }
    return self;
}

-(void)touchesBegan:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self];
    _left = point.x;
    _top = point.y;
    CGPoint topPoint = [touch locationInView:self.window];
    UIView *view = [self topView:self.window Point:topPoint];
    CGRect frame = [self.window convertRect:view.bounds fromView:view];
    _viewBound.frame = frame;
    [self.window addSubview:_viewBound];
    
    _viewInfoWindow.hidden = NO;
    _viewInfoLabel.text = [self viewInfo:view];
    
}

-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self.window];
    self.frame = CGRectMake(point.x-_left, point.y-_top, self.frame.size.width, self.frame.size.height);
    
    CGPoint topPoint = [touch locationInView:self.window];
    UIView *view = [self topView:self.window Point:topPoint];
    CGRect frame = [self.window convertRect:view.bounds fromView:view];
    _viewBound.frame = frame;
    _viewInfoWindow.hidden = NO;
    _viewInfoLabel.text = [self viewInfo:view];
}

-(void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [UIView animateWithDuration:0.5 animations:^{
            _viewInfoWindow.alpha = 0;
        } completion:^(BOOL finished) {
            _viewInfoWindow.hidden = YES;
            _viewInfoWindow.alpha = 1;
        }];
    });
}

-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [UIView animateWithDuration:0.5 animations:^{
            _viewInfoWindow.alpha = 0;
        } completion:^(BOOL finished) {
            _viewInfoWindow.hidden = YES;
            _viewInfoWindow.alpha = 1;
        }];
    });
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

-(NSString *)viewInfo:(UIView *)view{
    if (view) {
        NSMutableString *showString = [[NSMutableString alloc] init];
        NSString *tempString = [NSString stringWithFormat:@"  控件名称:%@",NSStringFromClass([view class])];
        [showString appendString:tempString];
        
        tempString = [NSString stringWithFormat:@"\n  控件位置:左:%0.1lf 上:%0.1lf 宽:%0.1lf 高:%0.1lf",view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height];
        [showString appendString:tempString];

        if([view isKindOfClass:[UILabel class]]){
            UILabel *vLabel = (UILabel *)view;
            tempString = [NSString stringWithFormat:@"\n  背景色:%@  字体颜色:%@   字体大小:%.f",[self hexFromUIColor:vLabel.backgroundColor],[self hexFromUIColor:vLabel.textColor],vLabel.font.pointSize];
            [showString appendString:tempString];
        }else if ([view isMemberOfClass:[UIView class]]) {
            tempString = [NSString stringWithFormat:@"\n  背景色:%@",[self hexFromUIColor:view.backgroundColor]];
            [showString appendString:tempString];
        }
        return showString;
    }
    return nil;
}

- (NSString *)hexFromUIColor: (UIColor*) color {
    if (!color) {
        return @"nil";
    }
    if(color == [UIColor clearColor]){
        return @"clear";
    }
    if (CGColorGetNumberOfComponents(color.CGColor) < 4) {
        const CGFloat *components = CGColorGetComponents(color.CGColor);
        color = [UIColor colorWithRed:components[0]
                                green:components[0]
                                 blue:components[0]
                                alpha:components[1]];
    }
    
    if (CGColorSpaceGetModel(CGColorGetColorSpace(color.CGColor)) != kCGColorSpaceModelRGB) {
        //return [NSString stringWithFormat:@"#FFFFFF"];
        return @"单色色彩空间模式";
    }
    
    int alpha = (int)((CGColorGetComponents(color.CGColor))[3]*255.0);
    NSString *hex = [NSString stringWithFormat:@"#%02X%02X%02X", (int)((CGColorGetComponents(color.CGColor))[0]*255.0),
                     (int)((CGColorGetComponents(color.CGColor))[1]*255.0),
                     (int)((CGColorGetComponents(color.CGColor))[2]*255.0)];
    if (alpha < 255) {//存在透明度
        hex = [NSString stringWithFormat:@"%@ alpha:%.2f",hex,(CGColorGetComponents(color.CGColor))[3]];
    }

    
    return hex;
}

@end
