//
//  DoraemonToastUtil.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import <Foundation/Foundation.h>

@interface DoraemonToastUtil : NSObject

+ (void)showToast:(NSString *)text;

+ (void)showToast:(NSString *)text inView:(UIView *)superView;

@end
