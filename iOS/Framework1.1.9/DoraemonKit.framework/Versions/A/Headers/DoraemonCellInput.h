//
//  DoraemonCellInput.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/7.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonCellInput : UIView

@property (nonatomic, strong) UITextField *textField;

- (void)renderUIWithTitle:(NSString *)title;

- (void)renderUIWithPlaceholder:(NSString *)placeholder;

- (void)needTopLine;

- (void)needDownLine;

@end

NS_ASSUME_NONNULL_END
