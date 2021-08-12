//
//  DoraemonViewCheckView.m
//  DoraemonKit
//
//  Created by yixiang on 2018/3/28.
//

#import "DoraemonViewCheckView.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"
#import <objc/runtime.h>


static CGFloat const kViewCheckSize = 62;

@interface DoraemonViewCheckView()

@property (nonatomic, strong) UIView *viewBound;//当前需要探测的view的边框
@property (nonatomic, strong) DoraemonVisualInfoWindow *infoWindow;//顶部被探测到的view的信息显示的UIwindow

@property (nonatomic, assign) CGFloat left;
@property (nonatomic, assign) CGFloat top;
@property (nonatomic, strong) NSMutableArray *arrViewHit;
@property (nonatomic, strong) UIView *oldView;

@end

@implementation DoraemonViewCheckView

-(instancetype)init{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(DoraemonScreenWidth/2-kViewCheckSize/2, DoraemonScreenHeight/2-kViewCheckSize/2, kViewCheckSize, kViewCheckSize);
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
        
        CGRect infoWindowFrame = CGRectZero;
        if (kInterfaceOrientationPortrait) {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30), DoraemonScreenWidth - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        } else {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        }
        _infoWindow = [[DoraemonVisualInfoWindow alloc] initWithFrame:infoWindowFrame];
        
    }
     
    return self;
}

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

    if ([self needRefresh:view]) {
        _infoWindow.infoAttributedText = [self viewInfo:view];
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
    if ([self needRefresh:view]) {
        _infoWindow.infoAttributedText = [self viewInfo:view];
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
    _infoWindow.hidden = NO;
    self.hidden = NO;
}

- (void)hide {
    [_viewBound removeFromSuperview];
    _infoWindow.hidden = YES;
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

-(NSMutableAttributedString *)viewInfo:(UIView *)view{
    if (view) {
        //获取属性名
        UIView *tempView = view;
        NSString *ivarName = nil;
        while(tempView != nil && tempView != self.doraemon_viewController.view) {
            ivarName =  [self nameWithInstance:view inTarger:tempView.superview];
            if (ivarName) {
                break;
            }
            tempView = tempView.superview;
        }
        if (!ivarName) {
            ivarName = [self nameWithInstance:view inTarger:self.doraemon_viewController.view];
        }
        
        if (!ivarName) {
            ivarName = [self nameWithInstance:view inTarger:view.doraemon_viewController];
        }
        
        NSMutableString *showString = [[NSMutableString alloc] init];
        NSString *tempString = nil;
        if (ivarName) {
            tempString = [NSString stringWithFormat:@"%@:%@(%@)",DoraemonLocalizedString(@"控件名称"),NSStringFromClass([view class]),ivarName];
        }else{
            tempString = [NSString stringWithFormat:@"%@:%@",DoraemonLocalizedString(@"控件名称"),NSStringFromClass([view class])];
        }
        NSLog(@"tempString == %@",tempString);
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
        
        NSString *string = [NSString stringWithFormat:@"%@",showString];
        // 行间距
        NSMutableParagraphStyle *style = [NSMutableParagraphStyle new];
        style.lineSpacing = kDoraemonSizeFrom750_Landscape(12);
        

        style.lineBreakMode = NSLineBreakByTruncatingTail;
        
        NSMutableAttributedString *attrString = [[NSMutableAttributedString alloc] initWithString:string];
        [attrString addAttributes:@{
                                    NSParagraphStyleAttributeName : style,
                                    NSFontAttributeName : [UIFont systemFontOfSize: kDoraemonSizeFrom750_Landscape(24)],
                                    NSForegroundColorAttributeName : [UIColor doraemon_black_1]
                                    }
                            range:NSMakeRange(0, string.length)];
        return attrString;
    }
    return nil;
}

- (NSString *)nameWithInstance:(id)instance inTarger:(id)target{
    unsigned int numIvars = 0;
    NSString *key=nil;
    Ivar * ivars = class_copyIvarList([target class], &numIvars);
    for(int i = 0; i < numIvars; i++) {
        Ivar thisIvar = ivars[i];
        const char *type = ivar_getTypeEncoding(thisIvar);
        NSString *stringType =  [NSString stringWithCString:type encoding:NSUTF8StringEncoding];
        if (![stringType hasPrefix:@"@"]) {
            continue;
        }
        if ((object_getIvar(target, thisIvar) == instance)) {
            key = [NSString stringWithUTF8String:ivar_getName(thisIvar)];
            break;
        }
    }
    free(ivars);
    return key;
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
