//
//  DoraemonHealthCountdownWindow.m
//  AFNetworking
//
//  Created by didi on 2020/1/9.
//

#import "DoraemonHealthCountdownWindow.h"
#import "DoraemonStatusBarViewController.h"
#import "DoraemonDefine.h"

@interface DoraemonHealthCountdownWindow()

@property (nonatomic, assign) CGFloat showViewSize;
@property (nonatomic, assign) int height;
@property (nonatomic, assign) int width;
@property (nonatomic, strong) UILabel *numberLabel;
@property (nonatomic, strong) NSTimer *timer;
@property (nonatomic, assign) NSInteger count;

@end

@implementation DoraemonHealthCountdownWindow

+ (DoraemonHealthCountdownWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonHealthCountdownWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonHealthCountdownWindow alloc] init];
    });
    return instance;
}

- (instancetype)init{
    _showViewSize = kDoraemonSizeFrom750_Landscape(100);
    CGFloat x = DoraemonScreenWidth - _showViewSize;
    CGFloat y = DoraemonScreenHeight/5;
    
    self = [super initWithFrame:CGRectMake(x, y, _showViewSize, _showViewSize)];
    if (self) {
    #if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            for (UIWindowScene* windowScene in [UIApplication sharedApplication].connectedScenes){
                if (windowScene.activationState == UISceneActivationStateForegroundActive){
                    self.windowScene = windowScene;
                    break;
                }
            }
        }
    #endif
        _numberLabel = [[UILabel alloc] initWithFrame:CGRectMake(0, 0, _showViewSize, _showViewSize)];
        _numberLabel.textColor = [UIColor doraemon_colorWithString:@"#3CBCA3"];
        _numberLabel.font = [UIFont systemFontOfSize:_showViewSize*2/5];
        _numberLabel.textAlignment = NSTextAlignmentCenter;
        [self addSubview:_numberLabel];
        self.backgroundColor = [UIColor clearColor];
        self.windowLevel = UIWindowLevelStatusBar + 50;
        self.layer.cornerRadius = _showViewSize/2;
        self.layer.masksToBounds = YES;//保证显示
        self.layer.borderWidth = _showViewSize/20;
        self.layer.borderColor = _numberLabel.textColor.CGColor;
        NSString *version= [UIDevice currentDevice].systemVersion;
        if(version.doubleValue >=10.0) {
            if (!self.rootViewController) {
                self.rootViewController = [[UIViewController alloc] init];
            }
        }else{
            //iOS9.0的系统中，新建的window设置的rootViewController默认没有显示状态栏
            if (!self.rootViewController) {
                self.rootViewController = [[DoraemonStatusBarViewController alloc] init];
            }
        }
        UIPanGestureRecognizer *move = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(_move:)];
        [self addGestureRecognizer:move];
    }
    return self;
}

- (void)_move:(UIPanGestureRecognizer *)sender{
    //1、获得拖动位移
    CGPoint offsetPoint = [sender translationInView:sender.view];
    //2、清空拖动位移
    [sender setTranslation:CGPointZero inView:sender.view];
    //3、重新设置控件位置
    UIView *panView = sender.view;
    CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
    CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
    if (newX < _showViewSize/2) {
        newX = _showViewSize/2;
    }
    if (newX > DoraemonScreenWidth - _showViewSize/2) {
        newX = DoraemonScreenWidth - _showViewSize/2;
    }
    if (newY < _showViewSize/2) {
        newY = _showViewSize/2;
    }
    if (newY > DoraemonScreenHeight - _showViewSize/2) {
        newY = DoraemonScreenHeight - _showViewSize/2;
    }
    panView.center = CGPointMake(newX, newY);
}


- (void)secondBtnAction{
    if (!_timer) {
        _timer = [NSTimer scheduledTimerWithTimeInterval:1.0 target:self selector:@selector(handleTimer) userInfo:nil repeats:YES];
        [_timer fire];
    }
}
//定时操作，更新UI
- (void)handleTimer {
    if (self.count < 0) {
        [self hide];
    } else {
        self.numberLabel.text = [NSString stringWithFormat:@"%zi",self.count];
    }
    self.count--;
}

- (void)start:(NSInteger)number{
    self.hidden = NO;
    _count = number > 0 ? number: 10;
    [self secondBtnAction];
}

- (void)hide{
    self.hidden = YES;
    [_timer invalidate];
    _timer = nil;
}

- (NSInteger)getCountdown{
    return _count;
}

@end
