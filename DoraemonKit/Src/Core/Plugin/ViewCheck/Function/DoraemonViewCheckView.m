//
//  DoraemonViewCheckView.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import "DoraemonViewCheckView.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"

static CGFloat const kViewCheckSize = 62;

@interface DoraemonViewCheckView()

@property (nonatomic, strong) UIView *viewBound;//当前需要探测的view的边框
@property (nonatomic, strong) DoraemonVisualInfoWindow *viewInfoWindow;//顶部被探测到的view的信息显示的UIwindow
@property (nonatomic, strong) UILabel *viewInfoLabel;//顶部被探测到的view的信息显示
@property (nonatomic, strong) UIButton *closeBtn;

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
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.bounds];
        imageView.image = [UIImage doraemon_imageNamed:@"doraemon_visual"];
        [self addSubview:imageView];
        
        _arrViewHit = [[NSMutableArray alloc] initWithCapacity:30];
        
        _viewBound = [[UIView alloc] init];
        _viewBound.layer.masksToBounds = YES;
        _viewBound.layer.borderWidth = 2.;
        _viewBound.layer.borderColor = [UIColor doraemon_colorWithHex:0xCC3A4B].CGColor;
        _viewBound.layer.zPosition = FLT_MAX;
        
        _viewInfoWindow = [[DoraemonVisualInfoWindow alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(30), DoraemonScreenHeight - kDoraemonSizeFrom750(180) - kDoraemonSizeFrom750(30), DoraemonScreenWidth - 2*kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(180))];
       [_viewInfoWindow makeKeyAndVisible];
        _viewInfoWindow.hidden = YES;
       
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(0.2 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
            [self.window makeKeyAndVisible];//将keyWindow还是交给原来那个
        });
        
        CGFloat closeWidth = kDoraemonSizeFrom750(44);
        CGFloat closeHeight = kDoraemonSizeFrom750(44);
        _closeBtn = [[UIButton alloc] initWithFrame:CGRectMake(_viewInfoWindow.bounds.size.width - closeWidth - kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(18), closeWidth, closeHeight)];
        [_closeBtn setBackgroundImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
        [_closeBtn addTarget:self action:@selector(closeBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [_viewInfoWindow addSubview:_closeBtn];
        
        _viewInfoLabel = [[UILabel alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750(32), kDoraemonSizeFrom750(30), _viewInfoWindow.bounds.size.width - 2*kDoraemonSizeFrom750(32) - _closeBtn.doraemon_width , _viewInfoWindow.bounds.size.height - 2*kDoraemonSizeFrom750(30))];
        _viewInfoLabel.numberOfLines = 0;
        _viewInfoLabel.backgroundColor =[UIColor clearColor];
        _viewInfoLabel.textColor = [UIColor doraemon_black_1];
        _viewInfoLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)];
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
    
//    _viewInfoWindow.hidden = NO;
    _viewInfoLabel.attributedText = [self viewInfo:view];
    
}

-(void)touchesMoved:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    UITouch *touch = [touches anyObject];
    CGPoint point = [touch locationInView:self.window];
    self.frame = CGRectMake(point.x-_left, point.y-_top, self.frame.size.width, self.frame.size.height);
    
    CGPoint topPoint = [touch locationInView:self.window];
    UIView *view = [self topView:self.window Point:topPoint];
    CGRect frame = [self.window convertRect:view.bounds fromView:view];
    _viewBound.frame = frame;
//    _viewInfoWindow.hidden = NO;
    _viewInfoLabel.attributedText = [self viewInfo:view];
}

-(void)touchesCancelled:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        [UIView animateWithDuration:0.5 animations:^{
//            _viewInfoWindow.alpha = 0;
//        } completion:^(BOOL finished) {
//            _viewInfoWindow.hidden = YES;
//            _viewInfoWindow.alpha = 1;
//        }];
//    });
}

-(void)touchesEnded:(NSSet<UITouch *> *)touches withEvent:(UIEvent *)event{
    [_viewBound removeFromSuperview];
//    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
//        [UIView animateWithDuration:0.5 animations:^{
//            _viewInfoWindow.alpha = 0;
//        } completion:^(BOOL finished) {
//            _viewInfoWindow.hidden = YES;
//            _viewInfoWindow.alpha = 1;
//        }];
//    });
}

- (void)closeBtnClicked:(id)sender {
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];
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
    _viewInfoWindow.hidden = NO;
    self.hidden = NO;
}

- (void)hide {
    [_viewBound removeFromSuperview];
    _viewInfoWindow.hidden = YES;
    self.hidden = YES;
}

-(NSMutableAttributedString *)viewInfo:(UIView *)view{
    if (view) {
        NSMutableString *showString = [[NSMutableString alloc] init];
        NSString *tempString = [NSString stringWithFormat:DoraemonLocalizedString(@"控件名称：%@"),NSStringFromClass([view class])];
        [showString appendString:tempString];
        
        tempString = [NSString stringWithFormat:DoraemonLocalizedString(@"\n控件位置：左%0.1lf  上%0.1lf  宽%0.1lf  高%0.1lf"),view.frame.origin.x,view.frame.origin.y,view.frame.size.width,view.frame.size.height];
        [showString appendString:tempString];
        
        if([view isKindOfClass:[UILabel class]]){
            UILabel *vLabel = (UILabel *)view;
            tempString = [NSString stringWithFormat:DoraemonLocalizedString(@"\n背景颜色：%@  字体颜色：%@  字体大小：%.f"),[self hexFromUIColor:vLabel.backgroundColor],[self hexFromUIColor:vLabel.textColor],vLabel.font.pointSize];
            [showString appendString:tempString];
        }else if ([view isMemberOfClass:[UIView class]]) {
            tempString = [NSString stringWithFormat:DoraemonLocalizedString(@"\n背景颜色：%@"),[self hexFromUIColor:view.backgroundColor]];
            [showString appendString:tempString];
        }
        
        NSString *string = [NSString stringWithFormat:showString];
        // 行间距
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.lineSpacing = kDoraemonSizeFrom750(12);
        style.lineBreakMode = NSLineBreakByTruncatingTail;
        
        NSMutableAttributedString *attrString = [[NSMutableAttributedString alloc] initWithString:string];
        [attrString addAttributes:@{
                                    NSParagraphStyleAttributeName : style,
                                    NSFontAttributeName : [UIFont systemFontOfSize:kDoraemonSizeFrom750(24)],
                                    NSForegroundColorAttributeName : [UIColor doraemon_black_1]
                                    }
                            range:NSMakeRange(0, string.length)];
        return attrString;
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
        return DoraemonLocalizedString(@"单色色彩空间模式");
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
