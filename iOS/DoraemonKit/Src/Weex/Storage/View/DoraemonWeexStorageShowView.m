//
//  DoraemonWeexStorageShowView.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStorageShowView.h"
#import "DoraemonDefine.h"

@interface DoraemonWeexStorageShowView()

@property (nonatomic, strong) UITextView *displayLabel;


@end

@implementation DoraemonWeexStorageShowView

- (instancetype)initWithFrame:(CGRect)frame{
    self = [super initWithFrame:frame];
    if (self) {
        _displayLabel = [[UITextView alloc] init];
        //_displayLabel.numberOfLines = 0;
        _displayLabel.textAlignment = NSTextAlignmentCenter; 
        _displayLabel.backgroundColor = [UIColor doraemon_black_2];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            _displayLabel.textColor = [UIColor labelColor];
        }
#endif
        [self addSubview:_displayLabel];
    }
    return self;
}

- (void)showText:(NSString *)text{
    _displayLabel.frame = CGRectMake(self.doraemon_width/2-150/2, self.doraemon_height/2-100/2, 75, 100);
    __weak typeof(self) weakSelf = self;
    [UIView animateWithDuration:0.25 animations:^{
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        strongSelf.displayLabel.frame = CGRectMake(self.doraemon_width/2-300/2, self.doraemon_height/2-400/2, 300, 400);
    } completion:^(BOOL finished) {
        __strong __typeof(weakSelf) strongSelf = weakSelf;
        strongSelf.displayLabel.text = text;
    }];
}


@end
