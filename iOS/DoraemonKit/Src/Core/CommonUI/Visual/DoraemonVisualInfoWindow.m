//
//  DoraemonVisualInfoWindow.m
//  DoraemonKit
//
//  Created by wenquan on 2018/12/5.
//

#import "DoraemonVisualInfoWindow.h"
#import "DoraemonDefine.h"

@interface DoraemonVisualInfoViewController : UIViewController
@property (nonatomic, strong) UIButton *closeBtn;
@property (nonatomic, strong) UILabel *infoLabel;
@end

@implementation DoraemonVisualInfoViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self initUI];
}

- (void)traitCollectionDidChange:(UITraitCollection *)previousTraitCollection {
    [super traitCollectionDidChange:previousTraitCollection];
    // trait发生了改变
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        if ([self.traitCollection hasDifferentColorAppearanceComparedToTraitCollection:previousTraitCollection]) {
            if (UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                [self.closeBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close_dark"] forState:UIControlStateNormal];
            } else {
                [self.closeBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
            }
        }
    }
#endif
}

- (void)initUI {
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        self.view.backgroundColor = [UIColor colorWithDynamicProvider:^UIColor * _Nonnull(UITraitCollection * _Nonnull traitCollection) {
            if (traitCollection.userInterfaceStyle == UIUserInterfaceStyleLight) {
                return [UIColor whiteColor];
            } else {
                return [UIColor secondarySystemBackgroundColor];
            }
        }];
    }
#endif
    
    // 视图加载完成之前拿不到当前window，所以这里等待 UI 线程执行完成 
    dispatch_async(dispatch_get_main_queue(), ^{
        CGSize viewSize = self.view.window.bounds.size;
        
        CGFloat closeWidth = kDoraemonSizeFrom750_Landscape(44);
        CGFloat closeHeight = kDoraemonSizeFrom750_Landscape(44);
        self.closeBtn = [[UIButton alloc] initWithFrame:CGRectMake(viewSize.width - closeWidth - kDoraemonSizeFrom750_Landscape(16), kDoraemonSizeFrom750_Landscape(16), closeWidth, closeHeight)];
        
        UIImage *closeImage = [UIImage doraemon_imageNamed:@"doraemon_close"];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            if (UITraitCollection.currentTraitCollection.userInterfaceStyle == UIUserInterfaceStyleDark) {
                closeImage = [UIImage doraemon_imageNamed:@"doraemon_close_dark"];
            }
        }
#endif
        
        [self.closeBtn setBackgroundImage:closeImage forState:UIControlStateNormal];
        [self.closeBtn addTarget:self action:@selector(closeBtnClicked:) forControlEvents:UIControlEventTouchUpInside];
        [self.view addSubview:self.closeBtn];
        
        self.infoLabel = [[UILabel alloc] initWithFrame:CGRectMake(kDoraemonSizeFrom750_Landscape(32), 0, viewSize.width - kDoraemonSizeFrom750_Landscape(32 + 16) - closeWidth , viewSize.height)];
        self.infoLabel.backgroundColor = [UIColor clearColor];
        self.infoLabel.textColor = [UIColor doraemon_black_1];
        self.infoLabel.font = [UIFont systemFontOfSize:kDoraemonSizeFrom750_Landscape(24)];
        self.infoLabel.numberOfLines = 0;
        [self.view addSubview:self.infoLabel];
        
        [(id)self.view.window setInfoLabel:self.infoLabel];
    });
}

#pragma mark - Actions
- (void)closeBtnClicked:(id)sender {
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];
}

- (void)viewWillTransitionToSize:(CGSize)size withTransitionCoordinator:(id<UIViewControllerTransitionCoordinator>)coordinator {
    [super viewWillTransitionToSize:size withTransitionCoordinator:coordinator];
    dispatch_async(dispatch_get_main_queue(), ^{
        self.view.window.frame = CGRectMake(kDoraemonSizeFrom750_Landscape(30), DoraemonScreenHeight - self.infoLabel.frame.size.height - kDoraemonSizeFrom750_Landscape(30), size.height, size.width);
    });
}

@end

@interface DoraemonVisualInfoWindow ()
@property (nonatomic, weak) UILabel *infoLabel;
@end

@implementation DoraemonVisualInfoWindow

#pragma mark - set

- (void)setInfoText:(NSString *)infoText {
    _infoText = infoText;
    _infoLabel.text = infoText;
}
- (void)setInfoAttributedText:(NSAttributedString *)infoAttributedText {
    _infoAttributedText = infoAttributedText;
    _infoLabel.attributedText = infoAttributedText;
}

#pragma mark - Lifecycle

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit {
    self.backgroundColor = [UIColor whiteColor];
    self.layer.cornerRadius = kDoraemonSizeFrom750_Landscape(8);
    self.layer.borderWidth = 1.;
    self.layer.borderColor = [UIColor doraemon_colorWithHex:0x999999 andAlpha:0.2].CGColor;
    self.windowLevel = UIWindowLevelAlert;
    self.rootViewController = [[DoraemonVisualInfoViewController alloc] init];
    
    UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
    [self addGestureRecognizer:pan];
}

#pragma mark - Override

- (void)becomeKeyWindow {
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

#pragma mark - Actions

- (void)pan:(UIPanGestureRecognizer *)sender{
    UIView *panView = sender.view;
    
    if (!panView.hidden) {
        //1、获得拖动位移
        CGPoint offsetPoint = [sender translationInView:sender.view];
        //2、清空拖动位移
        [sender setTranslation:CGPointZero inView:sender.view];
        //3、重新设置控件位置
        CGFloat newX = panView.doraemon_centerX+offsetPoint.x;
        CGFloat newY = panView.doraemon_centerY+offsetPoint.y;
        
        CGPoint centerPoint = CGPointMake(newX, newY);
        panView.center = centerPoint;
    }
}

@end
