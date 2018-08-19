//
//  DoraemonLoggerConsoleTipView.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>

@interface DoraemonLoggerConsoleTipView : UIView

- (void)showCurrentLog:(NSString *)log;

- (void)showCurrentLogColor:(UIColor *)logColor;
@end
