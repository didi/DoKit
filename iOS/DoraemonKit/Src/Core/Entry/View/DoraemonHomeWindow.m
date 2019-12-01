//
//  DoraemonHomeWindow.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/16.
//

#import "DoraemonHomeWindow.h"
#import "DoraemonDefine.h"
#import "UIColor+Doraemon.h"
#import "DoraemonHomeEntry.h"

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
    }
    return self;
}

- (void)openPlugin:(UIViewController *)vc{
    [self setRootVc:vc];
     self.hidden = NO;
}

- (void)show{
    DoraemonHomeEntry *vc = [[DoraemonHomeEntry alloc] init];
    [self setRootVc:vc];
    
    self.hidden = NO;
}

- (void)hide{
    [self setRootVc:nil];
    
    self.hidden = YES;
}

- (void)setRootVc:(UIViewController *)rootVc{
    if (rootVc) {
        UINavigationController *nav = [[UINavigationController alloc] initWithRootViewController:rootVc];
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
