//
//  DoraemonToastUtil.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/5/4.
//

#import "DoraemonToastUtil.h"
#import "UIColor+Doraemon.h"
#import "UIView+Doraemon.h"


@implementation DoraemonToastUtil

+ (void)showToast:(NSString *)text{
    [DoraemonToastUtil showToast:text inView:[UIApplication sharedApplication].keyWindow];
}

+ (void)showToast:(NSString *)text inView:(UIView *)superView {
    if (!superView) {
        return;
    }
    
    UILabel *label = [[UILabel alloc] init];
    label.font = [UIFont systemFontOfSize:14];
    label.text = text;
    [label sizeToFit];
    //label.backgroundColor = [UIColor doraemon_colorWithString:@"#33000000"];
    label.textColor = [UIColor blackColor];
    label.frame = CGRectMake(superView.doraemon_width/2-label.doraemon_width/2, superView.doraemon_height/2-label.doraemon_height/2, label.doraemon_width, label.doraemon_height);
    [superView addSubview:label];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [label removeFromSuperview];
    });
    
}

@end
