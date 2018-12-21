//
//  DoraemonStateBar.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/7.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN


typedef NS_ENUM(NSUInteger, DoraemonStateBarFrom) {
    DoraemonStateBarFromNSLog = 0,
    DoraemonStateBarFromCocoaLumberjack
};

@interface DoraemonStateBar : UIWindow

+ (DoraemonStateBar *)shareInstance;

- (void)show;

- (void)renderUIWithContent:(NSString *)content from:(DoraemonStateBarFrom)from;

- (void)hide;

@end

NS_ASSUME_NONNULL_END
