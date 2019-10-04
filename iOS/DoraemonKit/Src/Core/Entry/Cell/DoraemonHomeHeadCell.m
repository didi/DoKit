//
//  DoraemonHomeHeadCell.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import "DoraemonHomeHeadCell.h"

@implementation DoraemonHomeHeadCell

- (UILabel *)title {
    if (!_title) {
        _title = [UILabel new];
    }
    
    return _title;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        if (@available(iOS 13.0, *)) {
            self.backgroundColor = [UIColor systemBackgroundColor];
        } else {
            self.backgroundColor = [UIColor whiteColor];
        }
        [self addSubview:self.title];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.title.frame = UIEdgeInsetsInsetRect(self.bounds, UIEdgeInsetsMake(0, 15, 0, 15));
}

@end
