//
//  DoraemonEntryView.m
//  DoraemonKitDemo
//
//  Created by yixiang on 2017/12/11.
//  Copyright © 2017年 yixiang. All rights reserved.
//

#import "DoraemonEntryView.h"
#import "DoraemonDefine.h"
#import "DoraemonUtil.h"
#import "UIView+Doraemon.h"
#import "UIImage+Doraemon.h"
#import "DoraemonDefine.h"
#import "DoraemonHomeWindow.h"
#import "DoraemonStatusBarViewController.h"
@interface DoraemonEntryView()

@property (nonatomic, assign) BOOL isOpen;
@property (nonatomic, strong) UIButton *entryBtn;
@property (nonatomic, assign) CGFloat kEntryViewSize;

@end

@implementation DoraemonEntryView

- (instancetype)init{
    _kEntryViewSize = 58;
    self = [super initWithFrame:CGRectMake(0, DoraemonScreenHeight/3, _kEntryViewSize, _kEntryViewSize)];
    if (self) { 
        self.backgroundColor = [UIColor clearColor];
        self.windowLevel = UIWindowLevelStatusBar + 100.f;
        self.layer.masksToBounds = YES;
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
        
        UIButton *entryBtn = [[UIButton alloc] initWithFrame:self.bounds];
        entryBtn.backgroundColor = [UIColor clearColor];
        [entryBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_logo"] forState:UIControlStateNormal];
        entryBtn.layer.cornerRadius = 20.;
        [entryBtn addTarget:self action:@selector(entryClick:) forControlEvents:UIControlEventTouchUpInside];
        [self.rootViewController.view addSubview:entryBtn];
        _entryBtn = entryBtn; 
        UIPanGestureRecognizer *pan = [[UIPanGestureRecognizer alloc] initWithTarget:self action:@selector(pan:)];
        [self addGestureRecognizer:pan];
    }
    return self;
}

- (void)showClose:(NSNotification *)notification{
    NSDictionary *userInfo = notification.userInfo;
    [_entryBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_close"] forState:UIControlStateNormal];
    [_entryBtn removeTarget:self action:@selector(showClose:) forControlEvents:UIControlEventTouchUpInside];
    [_entryBtn addTarget:self action:@selector(closePluginClick:) forControlEvents:UIControlEventTouchUpInside];
}

- (void)closePluginClick:(UIButton *)btn{
    [_entryBtn setImage:[UIImage doraemon_imageNamed:@"doraemon_logo"] forState:UIControlStateNormal];
    [_entryBtn removeTarget:self action:@selector(closePluginClick:) forControlEvents:UIControlEventTouchUpInside];
    [_entryBtn addTarget:self action:@selector(entryClick:) forControlEvents:UIControlEventTouchUpInside];
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];
}

//不能让该View成为keyWindow，每一次它要成为keyWindow的时候，都要将appDelegate的window指为keyWindow
- (void)becomeKeyWindow{
    UIWindow *appWindow = [[UIApplication sharedApplication].delegate window];
    [appWindow makeKeyWindow];
}

/**
 进入工具主面板
 */
- (void)entryClick:(UIButton *)btn{
    if ([DoraemonHomeWindow shareInstance].hidden) {
        [[DoraemonHomeWindow shareInstance] show];
    }else{
        [[DoraemonHomeWindow shareInstance] hide];
    }
    
    [[NSNotificationCenter defaultCenter] postNotificationName:DoraemonClosePluginNotification object:nil userInfo:nil];
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
    if (newX < _kEntryViewSize/2) {
        newX = _kEntryViewSize/2;
    }
    if (newX > DoraemonScreenWidth - _kEntryViewSize/2) {
        newX = DoraemonScreenWidth - _kEntryViewSize/2;
    }
    if (newY < _kEntryViewSize/2) {
        newY = _kEntryViewSize/2;
    }
    if (newY > DoraemonScreenHeight - _kEntryViewSize/2) {
        newY = DoraemonScreenHeight - _kEntryViewSize/2;
    }
    panView.center = CGPointMake(newX, newY);
}

@end
