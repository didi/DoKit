//
//  DoraemonUIProfileWindow.h
//  DoraemonKit
//
//  Created by xgb on 2019/8/1.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonUIProfileWindow : UIWindow

+ (instancetype)sharedInstance;
- (void)showWithDepthText:(NSString *)text
               detailInfo:(NSString *)detail;
- (void)hide;

@end

NS_ASSUME_NONNULL_END
