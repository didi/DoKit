//
//  DoraemonSwitchView.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import <UIKit/UIKit.h>

@protocol DoraemonSwitchViewDelegate<NSObject>

- (void)changeSwitchOn:(BOOL)on;

@end

@interface DoraemonSwitchView : UIView

@property (nonatomic, weak) id<DoraemonSwitchViewDelegate> delegate;

- (void)renderUIWithTitle:(NSString *)title switchOn:(BOOL)on;

- (void)needTopLine;

- (void)needDownLine;

@end
