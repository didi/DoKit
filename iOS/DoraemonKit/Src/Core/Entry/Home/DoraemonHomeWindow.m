//
//  DoraemonHomeWindow.m
//  DoraemonKit
//
//  Created by yixiang on 2018/5/16.
//

#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"
#import "UIColor+Doraemon.h"
#import "DoraemonHomeViewController.h"
#import "DoraemonNavigationController.h"

@interface DoraemonHomeWindow()

- (void)openPlugin:(UIViewController *)vc;

@end

@implementation DoraemonHomeWindow

+ (DoraemonHomeWindow *)shareInstance{
    static dispatch_once_t once;
    static DoraemonHomeWindow *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonHomeWindow alloc] initWithFrame:CGRectMake(0, 0, DoraemonScreenWidth, DoraemonScreenHeight)];
    });
    return instance;
}

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        self.windowLevel = UIWindowLevelStatusBar - 1.f;
        self.backgroundColor = [UIColor clearColor];
        self.hidden = YES;
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
    }
    return self;
}

- (void)openPlugin:(UIViewController *)vc{
    [self setRootVc:vc];
     self.hidden = NO;
}

- (void)show{
    DoraemonHomeViewController *vc = [[DoraemonHomeViewController alloc] init];
    [self setRootVc:vc];
    
    self.hidden = NO;
}

- (void)hide{
    if (self.rootViewController.presentedViewController) {
        [self.rootViewController.presentedViewController dismissViewControllerAnimated:NO completion:nil];
    }
    [self setRootVc:nil];
    
    self.hidden = YES;
}

- (void)setRootVc:(UIViewController *)rootVc{
    if (rootVc) {
        DoraemonNavigationController *nav = [[DoraemonNavigationController alloc] initWithRootViewController:rootVc];
        NSDictionary *attributesDic = @{
                                        NSForegroundColorAttributeName:[UIColor blackColor],
                                        NSFontAttributeName:[UIFont systemFontOfSize:18]
                                        };
        [nav.navigationBar setTitleTextAttributes:attributesDic];
        _nav = nav;
        
        self.rootViewController = nav;
    }else{
        self.rootViewController = nil;
        _nav = nil;
    }

}

+ (void)openPlugin:(UIViewController *)vc{
    [[self shareInstance] openPlugin:vc];
}

@end
