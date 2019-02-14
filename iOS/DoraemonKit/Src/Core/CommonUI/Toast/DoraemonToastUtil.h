//
//  DoraemonToastUtil.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import <Foundation/Foundation.h>


typedef void (^DoraemonHandleRestartActionBlock)(void);
typedef void (^DoraemonHandleRestartCancleActionBlock)(void);

@interface DoraemonToastUtil : NSObject

+ (void)showToast:(NSString *)text;

+ (void)showToast:(NSString *)text inView:(UIView *)superView;

+ (void)handleRestartActionWithVC:(UIViewController *)vc
                     restartBlock:(DoraemonHandleRestartActionBlock)restartBlock
                      cancleBlock:(DoraemonHandleRestartCancleActionBlock)cancleBlock;
@end
