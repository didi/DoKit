//
//  DoraemonSwitchView.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/13.
//

#import <UIKit/UIKit.h>

@protocol DoraemonSwitchViewDelegate<NSObject>

- (void)changeSwitchOn:(BOOL)on sender:(id)sender;

@end

@interface DoraemonCellSwitch : UIView

@property (nonatomic, weak) id<DoraemonSwitchViewDelegate> delegate;

@property (nonatomic, strong) UISwitch *switchView;

- (void)renderUIWithTitle:(NSString *)title switchOn:(BOOL)on;

- (void)needTopLine;

- (void)needDownLine;

@end
