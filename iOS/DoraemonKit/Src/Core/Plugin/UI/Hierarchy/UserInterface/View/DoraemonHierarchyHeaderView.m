//
//  DoraemonHierarchyHeaderView.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyHeaderView.h"
#import "UIView+Doraemon.h"

@interface DoraemonHierarchyHeaderView ()

@property (nonatomic, strong) UILabel *titleLabel;

@end

@implementation DoraemonHierarchyHeaderView

- (instancetype)init {
    if (self = [super init]) {
        [self initUI];
    }
    return self;
}

- (instancetype)initWithFrame:(CGRect)frame {
    if (self = [super initWithFrame:frame]) {
        [self initUI];
    }
    return self;
}

#pragma mark - Primary
- (void)initUI {    
    [self addSubview:self.titleLabel];
}

- (void)layoutSubviews {
    [super layoutSubviews];
    
    self.titleLabel.frame = CGRectMake(10, 0, self.doraemon_width - 10 * 2, self.doraemon_height);
}

#pragma mark - Getters and setters
- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont boldSystemFontOfSize:18];
        _titleLabel.textColor = [UIColor blackColor];
    }
    return _titleLabel;
}

@end
