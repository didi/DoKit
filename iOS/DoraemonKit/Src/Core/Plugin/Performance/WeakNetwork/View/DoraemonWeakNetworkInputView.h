//
//  DoraemonWeakNetworkInputView.h
//  AFNetworking
//
//  Created by didi on 2019/12/16.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonNetWeakInputBlock)(void);

@interface DoraemonWeakNetworkInputView : UIView

- (void)renderUIWithTitle:(NSString *)title end:(NSString *)epilog;
- (void)renderUIWithSpeed:(long)speed define:(NSInteger)value;
- (long)getInputValue;

- (void)addBlock:(DoraemonNetWeakInputBlock)block;

@end

NS_ASSUME_NONNULL_END
