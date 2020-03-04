//
//  DoraemonHealthAlertView.h
//  AFNetworking
//
//  Created by didi on 2020/1/8.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonHealthAlertOKActionBlock)(void);
typedef void (^DoraemonHealthAlertCancleActionBlock)(void);
typedef void (^DoraemonHealthAlertQuitActionBlock)(void);

@interface DoraemonHealthAlertView : UIView

- (void)renderUI:(NSString *)title placeholder:(NSArray*)placeholders inputTip:(NSArray*)inputTips ok:(NSString *)okText quit:(NSString *)quitText cancle:(NSString *)cancleText  okBlock:(DoraemonHealthAlertOKActionBlock)okBlock quitBlock:(DoraemonHealthAlertQuitActionBlock) quitBlock
cancleBlock:(DoraemonHealthAlertCancleActionBlock)cancleBlock ;

- (NSArray *)getInputText;

@end

NS_ASSUME_NONNULL_END
