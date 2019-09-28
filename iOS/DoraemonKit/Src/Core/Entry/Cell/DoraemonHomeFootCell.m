//
//  DoraemonHomeFootCell.m
//  DoraemonKit
//
//  Created by dengyouhua on 2019/9/4.
//

#import "DoraemonHomeFootCell.h"

@implementation DoraemonHomeFootCell

- (UILabel *)title {
    if (!_title) {
        _title = [UILabel new];
    }
    
    return _title;
}

- (instancetype)initWithFrame:(CGRect)frame {
    self = [super initWithFrame:frame];
    if (self) {
        [self addSubview:self.title];
    }
    return self;
}

- (void)layoutSubviews {
    [super layoutSubviews];
    self.title.frame = UIEdgeInsetsInsetRect(self.bounds, UIEdgeInsetsMake(0, 15, 0, 15));
}

@end
