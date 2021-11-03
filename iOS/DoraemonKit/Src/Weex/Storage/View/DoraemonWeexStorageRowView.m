//
//  DoraemonWeexStorageRowView.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import "DoraemonWeexStorageRowView.h"
#import "DoraemonDefine.h"

@implementation DoraemonWeexStorageRowView

- (instancetype)initWithFrame:(CGRect)frame{
    if (self = [super initWithFrame:frame]) {
        
    }
    return self;
}

- (void)setDataArray:(NSArray *)dataArray{
    _dataArray = dataArray;
    for (UIView *sub in self.subviews) {
        [sub removeFromSuperview];
    }
    for (NSInteger i = 0; i < self.dataArray.count; i++) {
        NSString *content = self.dataArray[i];
        UILabel *label = [[UILabel alloc] init];
        UIColor *color = [UIColor doraemon_colorWithString:@"#dcdcdc"];
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        if (@available(iOS 13.0, *)) {
            color = [UIColor doraemon_black_2];
            if (self.type == DoraemonWeexStorageRowViewTypeForOne) {
                color = [UIColor secondarySystemBackgroundColor];
            }
            if (self.type == DoraemonWeexStorageRowViewTypeForTwo) {
                color = [UIColor tertiarySystemBackgroundColor];
            }
        } else {
#endif
            if (self.type == DoraemonWeexStorageRowViewTypeForOne) {
                color = [UIColor doraemon_colorWithString:@"#e6e6e6"];
            }
            if (self.type == DoraemonWeexStorageRowViewTypeForTwo) {
                color = [UIColor doraemon_colorWithString:@"#ebebeb"];
            }
#if defined(__IPHONE_13_0) && (__IPHONE_OS_VERSION_MAX_ALLOWED >= __IPHONE_13_0)
        }
#endif
        label.backgroundColor = color;
        label.text = content;
        label.textAlignment = NSTextAlignmentCenter;
        label.tag = i;
        label.userInteractionEnabled = YES;
        [label addGestureRecognizer:[[UITapGestureRecognizer alloc] initWithTarget:self action:@selector(tapLabel:)]];
        [self addSubview:label];
    }
}

- (void)layoutSubviews{
    [super layoutSubviews];
    
    for (UIView *subView in self.subviews) {
        if ([subView isKindOfClass:UILabel.class]) {
            CGFloat width = (self.bounds.size.width - (self.dataArray.count - 1)) / self.dataArray.count;
            subView.frame = CGRectMake(subView.tag * (width + 1), 0, width, self.bounds.size.height);
        }
    }
}

- (void)tapLabel:(UITapGestureRecognizer *)tap{
    UILabel *label = (UILabel *)tap.view;
    if ([self.delegate respondsToSelector:@selector(rowView:didLabelTaped:)]) {
        [self.delegate rowView:self didLabelTaped:label];
    }
}


@end
