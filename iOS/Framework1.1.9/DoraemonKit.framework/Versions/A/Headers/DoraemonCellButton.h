//
//  DoraemonCellButton.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import <UIKit/UIKit.h>

@protocol DoraemonCellButtonDelegate<NSObject>

- (void)cellBtnClick:(id)sender;

@end

@interface DoraemonCellButton : UIView

@property (nonatomic, weak) id<DoraemonCellButtonDelegate> delegate;

- (void)renderUIWithTitle:(NSString *)title;

- (void)renderUIWithRightContent:(NSString *)rightContent;

- (void)needTopLine;

- (void)needDownLine;

@end
