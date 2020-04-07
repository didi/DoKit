//
//  DoraemonUIProfileWindow.m
//  DoraemonKit
//
//  Created by xgb on 2019/8/1.
//

#import "DoraemonUIProfileWindow.h"
#import "UIView+Doraemon.h"
#import "DoraemonDefine.h"

#define kWindowWidth        220
#define kExpandHeight       250
#define kTextHeight         30

@interface DoraemonUIProfileWindow ()

@property (nonatomic, strong) UILabel *lbText;
@property (nonatomic, strong) UITextView *textView;
@property (nonatomic, assign) CGRect storedFrame;

@end

@implementation DoraemonUIProfileWindow

+ (instancetype)sharedInstance
{
    static DoraemonUIProfileWindow *sharedInstance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        sharedInstance = [DoraemonUIProfileWindow new];
    });
    
    return sharedInstance;
}

- (instancetype)init
{
    self = [super init];
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
        self.backgroundColor = [UIColor whiteColor];
        self.layer.borderWidth = 2;
        self.layer.borderColor = [UIColor lightGrayColor].CGColor;
        self.windowLevel = UIWindowLevelStatusBar + 50.f;
        self.frame = CGRectMake(10, 65, kWindowWidth, kTextHeight);
        self.clipsToBounds = YES;
        
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [self addGestureRecognizer:pan];
        [self addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self
                                                                                 action:@selector(tap)]];
    }
    return self;
}

- (void)showWithDepthText:(NSString *)text
               detailInfo:(NSString *)detail
{
    self.lbText.text = text;
    self.textView.text = detail;
    [self addSubview:self.lbText];
    [self addSubview:self.textView];
    self.hidden = NO;
}

- (void)hide
{
    [self.lbText removeFromSuperview];
    [self.textView removeFromSuperview];
    self.hidden = YES;
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
    if (newX < kWindowWidth/2) {
        newX = kWindowWidth/2;
    }
    if (newX > DoraemonScreenWidth - kWindowWidth/2) {
        newX = DoraemonScreenWidth - kWindowWidth/2;
    }
    if (newY < self.doraemon_height/2) {
        newY = self.doraemon_height/2;
    }
    if (newY > DoraemonScreenHeight - self.doraemon_height/2) {
        newY = DoraemonScreenHeight - self.doraemon_height/2;
    }
    panView.center = CGPointMake(newX, newY);
}

- (void)tap
{
    if (CGRectIsEmpty(self.storedFrame)) {
        self.storedFrame = CGRectMake(self.frame.origin.x, self.frame.origin.y, self.frame.size.width, 180);
    }
    
    [UIView animateWithDuration:.25 animations:^{
        CGRect tmp = self.frame;
        self.frame = self.storedFrame;
        self.storedFrame = tmp;
    }];
}

#pragma mark - getters
- (UILabel *)lbText
{
    if (!_lbText) {
        _lbText = [UILabel new];
        _lbText.frame = CGRectMake(0, 0, kWindowWidth, kTextHeight);
        _lbText.backgroundColor = [UIColor lightGrayColor];
        _lbText.textAlignment = NSTextAlignmentCenter;
    }
    return _lbText;
}

- (UITextView *)textView
{
    if (!_textView) {
        _textView = [UITextView new];
        _textView.frame = CGRectMake(0, kTextHeight, kWindowWidth, kExpandHeight-kTextHeight);
        _textView.textAlignment = NSTextAlignmentCenter;
        _textView.editable = NO;
        _textView.userInteractionEnabled = NO;
        _textView.backgroundColor = [UIColor clearColor];
    }
    return _textView;
}

@end
