//
//  DoraemonHomeWindow.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/16.
//

#import <UIKit/UIKit.h>

@interface DoraemonHomeWindow : UIWindow

@property (nonatomic, strong) UINavigationController *nav;

+ (DoraemonHomeWindow *)shareInstance;

- (void)openPlugin:(UIViewController *)vc;

- (void)show;

- (void)hide;

@end
