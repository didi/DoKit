//
//  DoraemonAlertUtil.h
//  DoraemonKit
//
//  Created by didi on 2019/8/27.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonAlertOKActionBlock)(void);
typedef void (^DoraemonAlertCancleActionBlock)(void);

@interface DoraemonAlertUtil : NSObject

+ (void)handleAlertActionWithVC:(UIViewController *)vc
                     okBlock:(DoraemonAlertOKActionBlock)okBlock
                      cancleBlock:(DoraemonAlertCancleActionBlock)cancleBlock;


+ (void)handleAlertActionWithVC:(UIViewController *)vc
                             text:(NSString *)text
                     okBlock:(DoraemonAlertOKActionBlock)okBlock
                      cancleBlock:(DoraemonAlertCancleActionBlock)cancleBlock;

+ (void)handleAlertActionWithVC:(UIViewController *)vc
                           text:(NSString *)text
                        okBlock:(DoraemonAlertOKActionBlock)okBlock;

+ (void)handleAlertActionWithVC:(UIViewController *)vc
                          title: (NSString *)title
                           text:(NSString *)text
                             ok:(NSString *)ok
                        okBlock:(DoraemonAlertOKActionBlock)okBlock;

+ (void)handleAlertActionWithVC:(UIViewController *)vc
                          title: (NSString *)title
                           text:(NSString *)text
                             ok:(NSString *)ok
                         cancel:(NSString *)cancel
                        okBlock:(DoraemonAlertOKActionBlock)okBlock
                    cancleBlock:(DoraemonAlertCancleActionBlock)cancleBlock;



@end

NS_ASSUME_NONNULL_END
