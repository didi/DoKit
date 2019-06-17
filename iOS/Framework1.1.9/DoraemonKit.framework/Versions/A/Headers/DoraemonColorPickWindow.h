//
//  DoraemonColorPickWindow.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/3/5.
//

#import <UIKit/UIKit.h>

@interface DoraemonColorPickWindow : UIWindow

+ (DoraemonColorPickWindow *)shareInstance;

- (void)show;

- (void)hide;

@end
