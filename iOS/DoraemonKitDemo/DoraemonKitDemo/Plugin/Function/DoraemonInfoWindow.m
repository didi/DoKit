//
//  DoraemonInfoWindow.m
//  DoraemonKitDemo
//
//  Created by qian on 2021/5/5.
//  Copyright © 2021 yixiang. All rights reserved.
//

#import "DoraemonInfoWindow.h"
#import "DoraemonDefine.h"
#import "Doraemoni18NUtil.h"
#import "DoraemonVisualInfoWindow.h"
#import <objc/runtime.h>



static CGFloat const kViewCheckSize = 62;

@interface DoraemonInfoWindow()

@property (nonatomic, strong) DoraemonVisualInfoWindow *infoWindow;//顶部被探测到的view的信息显示的UIwindow

@property (nonatomic, assign) CGFloat left1;
@property (nonatomic, assign) CGFloat left2;

//@property (nonatomic, assign) CGFloat xleft;
//@property (nonatomic, assign) CGFloat ytop;
//@property (nonatomic, assign) CGFloat xwidth;
//@property (nonatomic, assign) CGFloat yheight;

//@property (nonatomic, strong) NSMutableArray *arrViewHit;
//@property (nonatomic, strong) UIView *oldView;


@end

@implementation DoraemonInfoWindow

+ (DoraemonInfoWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonInfoWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonInfoWindow alloc] init];
    });
    return instance;
}

//初始化位置放在屏幕中间
-(instancetype)init{
    self = [super init];
    if (self) {
        self.frame = CGRectMake(DoraemonScreenWidth/2-kViewCheckSize/2-30, DoraemonScreenHeight/2-kViewCheckSize/2, kViewCheckSize, kViewCheckSize);
        self.backgroundColor = [UIColor clearColor];
        self.layer.zPosition = FLT_MAX;
        
        UIImageView *imageView = [[UIImageView alloc] initWithFrame:self.bounds];
        imageView.image = [UIImage doraemon_xcassetImageNamed:@"doraemon_visual"];
        [self addSubview:imageView];
        
        
        CGRect infoWindowFrame = CGRectZero;
        if (kInterfaceOrientationPortrait) {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30)+200, DoraemonScreenWidth - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        } else {
            infoWindowFrame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - kDoraemonSizeFrom750_Landscape(180) - kDoraemonSizeFrom750_Landscape(30)+200, DoraemonScreenHeight - 2*kDoraemonSizeFrom750_Landscape(30), kDoraemonSizeFrom750_Landscape(180));
        }
        _infoWindow = [[DoraemonVisualInfoWindow alloc] initWithFrame:infoWindowFrame];
        
    }
     
    return self;
}

- (void)show {
    _infoWindow.hidden = NO;
    self.hidden = NO;
}

- (void)hide {
    _infoWindow.hidden = YES;
    self.hidden = YES;
}
-(void)setInfo:(UIView *)view:(int)choice{
    _infoWindow.infoAttributedText = [self viewInfo:view:choice];
}


- (CGRect)relativeFrameForScreenWithView:(UIView*)view{
    
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
    return CGRectMake(x, y, self.frame.size.width, self.frame.size.height);
}


-(NSMutableAttributedString *)viewInfo:(UIView *)view :(int)choice{
        if (view) {
            //获取属性名
            UIView *tempView = view;
            NSString *ivarName = nil;
            while(tempView != nil && tempView != self.viewController.view) {
                ivarName =  [self nameWithInstance:view inTarger:tempView.superview];
                if (ivarName) {
                    break;
                }
                tempView = tempView.superview;
            }
            if (!ivarName) {
                ivarName = [self nameWithInstance:view inTarger:self.viewController.view];
            }
            
            if (!ivarName) {
                ivarName = [self nameWithInstance:view inTarger:view.viewController];
            }
            CGRect CR=[self relativeFrameForScreenWithView:view];
            NSMutableString *showString = [[NSMutableString alloc] init];
            NSString *tempString = nil;
            if (ivarName) {
                tempString = [NSString stringWithFormat:@"%@:%@(%@)",DoraemonLocalizedString(@"控件名称"),NSStringFromClass([view class]),ivarName];
            }else{
                tempString = [NSString stringWithFormat:@"%@:%@",DoraemonLocalizedString(@"控件名称"),NSStringFromClass([view class])];
            }
            NSLog(@"tempString == %@",tempString);
            [showString appendString:tempString];
            //CGFloat xx2=[_viewCheckViewB xleft];
            
            //CGFloat yy2=[_viewCheckViewB ytop];
            //NSLog(@"%f",yy2);
            //CGFloat width2=[_viewCheckViewB xwidth];
            //CGFloat height2=[_viewCheckViewB yheight];
            if(choice==1) _left1=CR.origin.x;
            else _left2=CR.origin.x;
            tempString = [NSString stringWithFormat:DoraemonLocalizedString(@"\n控件位置：左%0.1lf  上%0.1lf  "),_left1,_left2];
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
