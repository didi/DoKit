//
//  DoraemonWeakNetworkInputView.h
//  AFNetworking
//
//  Created by didi on 2019/12/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWeakNetworkInputView : UIView

- (void)renderUIWithTitle:(NSString *)title end:(NSString *)epilog;
- (void)changeInput:(long)speed;
- (long)getInputValue;

@end

NS_ASSUME_NONNULL_END
