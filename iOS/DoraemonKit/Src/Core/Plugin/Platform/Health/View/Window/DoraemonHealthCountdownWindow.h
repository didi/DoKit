//
//  DoraemonHealthCountdownWindow.h
//  DoraemonKit
//
//  Created by didi on 2020/1/9.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHealthCountdownWindow : UIWindow


+ (DoraemonHealthCountdownWindow *)shareInstance;

- (void)start:(NSInteger)number;
- (void)hide;
- (NSInteger)getCountdown;

@end

NS_ASSUME_NONNULL_END
