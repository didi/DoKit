//
//  DoraemonColorPickInfoWindow.h
//  DoraemonKit
//
//  Created by wenquan on 2018/12/4.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonColorPickInfoWindow : UIWindow

+ (DoraemonColorPickInfoWindow *)shareInstance;

- (void)show;

- (void)hide;

- (void)setCurrentColor:(NSString *)hexColor;

@end

NS_ASSUME_NONNULL_END
