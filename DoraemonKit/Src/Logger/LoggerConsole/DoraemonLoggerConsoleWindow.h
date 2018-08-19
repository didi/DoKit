//
//  DoraemonLoggerConsoleWindow.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>

@interface DoraemonLoggerConsoleWindow : UIWindow

+ (DoraemonLoggerConsoleWindow *)shareInstance;

- (void)show;

- (void)hide;

@end
