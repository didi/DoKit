//
//  DoraemonColorPickInfoWindow.m
//  DoraemonKit
//
//  Created by wenquan on 2018/12/4.
//

#import "DoraemonColorPickInfoWindow.h"
#import "DoraemonColorPickInfoView.h"
#import "DoraemonDefine.h"

@interface DoraemonColorPickInfoWindow () <DoraemonColorPickInfoViewDelegate>

@property (nonatomic, strong) DoraemonColorPickInfoView *pickInfoView;

@end

@implementation DoraemonColorPickInfoWindow

#pragma mark - Lifecycle

+ (DoraemonColorPickInfoWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonColorPickInfoWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonColorPickInfoWindow alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super initWithFrame:CGRectMake(kDoraemonSizeFrom750(30), DoraemonScreenHeight - kDoraemonSizeFrom750(100) - kDoraemonSizeFrom750(30) - IPHONE_SAFEBOTTOMAREA_HEIGHT, DoraemonScreenWidth - 2*kDoraemonSizeFrom750(30), kDoraemonSizeFrom750(100))];
    if (self) {
        self.backgroundColor = [UIColor clearColor];
        self.windowLevel = UIWindowLevelAlert;
        if (!self.rootViewController) {
            self.rootViewController = [[UIViewController alloc] init];
        }
        
        DoraemonColorPickInfoView *pickInfoView = [[DoraemonColorPickInfoView alloc] initWithFrame:self.bounds];
        pickInfoView.delegate = self;
        [self.rootViewController.view addSubview:pickInfoView];
        self.pickInfoView = pickInfoView;
        
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [self addGestureRecognizer:pan];
        
        [[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(closePlugin:) name:DoraemonClosePluginNotification object:nil];
    }
    return self;
}

#pragma mark - Override

//不能让该View成为keyWindow，每一次它要成为keyWindow的时候，都要将appDelegate的window指为keyWindow
- (void)becomeKeyWindow {
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

#pragma mark - Public

- (void)show{
    self.hidden = NO;
}

- (void)hide{
    self.hidden = YES;
}

- (void)setCurrentColor:(NSString *)hexColor {
    [self.pickInfoView setCurrentColor:hexColor];
}

#pragma mark - Actions

- (void)pan:(UIPanGestureRecognizer *)sender{
    //1、获得拖动位移
    CGPoint offsetPoint = [sender translationInView:sender.view];
    //2、清空拖动位移
    [sender setTranslation:CGPointZero inView:sender.view];
    //3、重新设置控件位置
    UIView *panView = sender.view;
    CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
    CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
   
    CGPoint centerPoint = CGPointMake(newX, newY);
    panView.center = centerPoint;
}

#pragma mark - Delegate

#pragma mark DoraemonColorPickInfoViewDelegate

- (void)closeBtnClicked:(id)sender onColorPickInfoView:(DoraemonColorPickInfoView *)colorPickInfoView {
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];
}

#pragma mark - Notification

- (void)closePlugin:(NSNotification *)notification{
    [self hide];
}


@end
