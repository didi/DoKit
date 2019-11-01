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

+ (void)showToast:(NSString *)text inView:(UIView *)superView {
    if (!superView) {
        return;
    }
    
    UILabel *label = [[UILabel alloc] init];
    label.font = [UIFont systemFontOfSize:14];
    label.text = text;
    [label sizeToFit];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    if (@available(iOS 13.0, *)) {
        label.textColor = [UIColor labelColor];
    } else {
#endif
        label.textColor = [UIColor blackColor];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
    }
#endif
    label.frame = CGRectMake(superView.doraemon_width/2-label.doraemon_width/2, superView.doraemon_height/2-label.doraemon_height/2, label.doraemon_width, label.doraemon_height);
    [superView addSubview:label];
    dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(1.5 * NSEC_PER_SEC)), dispatch_get_main_queue(), ^{
        [label removeFromSuperview];
    });
    
}

@end
