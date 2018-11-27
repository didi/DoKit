//
//  DoraemonColorPickWindow.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/5.
//

#import "DoraemonColorPickWindow.h"
#import "UIView+DoraemonPositioning.h"
#import "DoraemonDefine.h"
#import "DoraemonColorPickView.h"
#import "UIImage+DoraemonKit.h"
#import "DoraemonDefine.h"
#import "UIColor+DoreamonKit.h"
#import "DoraemonDefine.h"
#import "DoraemonUtil.h"

static CGFloat const kColorPickWindowSize = 80;

@interface DoraemonColorPickWindow()

@property (nonatomic, strong) DoraemonColorPickView *colorPickView;

@end

@implementation DoraemonColorPickWindow

+ (DoraemonColorPickWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonColorPickWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonColorPickWindow alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super initWithFrame:CGRectMake(DoraemonScreenWidth/2-kColorPickWindowSize/2, DoraemonScreenHeight/2-kColorPickWindowSize/2, kColorPickWindowSize, kColorPickWindowSize)];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.windowLevel = UIWindowLevelStatusBar + 11.f;
        if (!self.rootViewController) {
            self.rootViewController = [[UIViewController alloc] init];
        }
        
        DoraemonColorPickView *colorPickView = [[DoraemonColorPickView alloc] initWithFrame:self.bounds];
        colorPickView.backgroundColor = [UIColor clearColor];
        [self.rootViewController.view addSubview:colorPickView];
        self.colorPickView = colorPickView;
        
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [self addGestureRecognizer:pan];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
    }
    return self;
}

- (void)show{
    self.hidden = NO;
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonShowPluginNotification object:nil userInfo:nil];
}

- (void)closePlugin:(NSNotification *)notification{
    self.hidden = YES;
}

- (void)hide{
    self.hidden = YES;
}

//不能让该View成为keyWindow，每一次它要成为keyWindow的时候，都要将appDelegate的window指为keyWindow
- (void)becomeKeyWindow{
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

- (void)pan:(UIPanGestureRecognizer *)sender{
    //1、获得拖动位移
    CGPoint offsetPoint = [sender translationInView:sender.view];
    //2、清空拖动位移
    [sender setTranslation:CGPointZero inView:sender.view];
    //3、重新设置控件位置
    UIView *panView = sender.view;
    CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
    CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
//    if (newX < kColorPickWindowSize/2) {
//        newX = kColorPickWindowSize/2;
//    }
//    if (newX > DoraemonScreenWidth - kColorPickWindowSize/2) {
//        newX = DoraemonScreenWidth - kColorPickWindowSize/2;
//    }
//    if (newY < kColorPickWindowSize/2) {
//        newY = kColorPickWindowSize/2;
//    }
//    if (newY > DoraemonScreenHeight - kColorPickWindowSize/2) {
//        newY = DoraemonScreenHeight - kColorPickWindowSize/2;
//    }
    
    CGPoint centerPoint = CGPointMake(newX, newY);
    panView.center = centerPoint;
    
    NSString *hexColor = [self getColorWithCenterPoint:centerPoint];
    [self.colorPickView setCurrentColor:hexColor];
}

- (NSString *)getColorWithCenterPoint:(CGPoint)centerPoint{
    //UIViewController *topVc = [DoraemonUtil topViewControllerForKeyWindow];
    //return [self getColorOfPoint:centerPoint InView:topVc.view];
    
    UIView *delegateWindow = [[UIApplication sharedApplication].delegate window];
    return [self getColorOfPoint:centerPoint InView:delegateWindow];
}

- (NSString *)getColorOfPoint:(CGPoint)point InView:(UIView*)view{
    unsigned char pixel[4] = {0};
    CGColorSpaceRef colorSpace = CGColorSpaceCreateDeviceRGB();
    CGContextRef context = CGBitmapContextCreate(pixel,
                                                 1, 1, 8, 4, colorSpace, (CGBitmapInfo)kCGImageAlphaPremultipliedLast);
    
    CGContextTranslateCTM(context, -point.x, -point.y);
    
    [view.layer renderInContext:context];
    
    CGContextRelease(context);
    CGColorSpaceRelease(colorSpace);
    
    NSString *hexColor = [NSString stringWithFormat:@"#%02x%02x%02x",pixel[0],pixel[1],pixel[2]];
    //NSLog(@"color == %@",hexColor);
    return hexColor;
}

@end
