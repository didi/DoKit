//
//  DoraemonToastUtil.h
//  DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import <Foundation/Foundation.h>
#import <UIKit/UIKit.h>

@interface DoraemonToastUtil : NSObject

+ (void)showToast:(NSString *)text inView:(UIView *)superView;
+ (void)showToastBlack:(NSString *)text inView:(UIView *)superView;

@end
