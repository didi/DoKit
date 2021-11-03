//
//  DoraemonHealthEndInputView.h
//  DoraemonKit
//
//  Created by didi on 2020/1/8.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHealthEndInputView : UIView

@property (nonatomic, strong) UILabel *label;
@property (nonatomic, strong) UITextField *textField;

- (void)renderUIWithTitle:(NSString *)tip placeholder:(NSString *)placeholder;

@end

NS_ASSUME_NONNULL_END
