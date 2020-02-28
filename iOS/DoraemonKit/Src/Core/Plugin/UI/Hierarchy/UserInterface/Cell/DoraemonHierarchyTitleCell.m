//
//  DoraemonHierarchyTitleCell.m
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyTitleCell.h"
#import "DoraemonHierarchyCellModel.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyTitleCell ()

@property (nonatomic, strong) UILabel *titleLabel;

@property (nonatomic, strong) NSLayoutConstraint *titleLabelBottomCons;

@end

@implementation DoraemonHierarchyTitleCell

#pragma mark - Life cycle
- (instancetype)initWithStyle:(UITableViewCellStyle)style reuseIdentifier:(NSString *)reuseIdentifier {
    if (self = [super initWithStyle:style reuseIdentifier:reuseIdentifier]) {
        [self initUI];
    }
    return self;
}

#pragma mark - Public
- (void)initUI {
    self.selectedBackgroundView = [[UIView alloc] init];
    self.selectionStyle = UITableViewCellSelectionStyleNone;
    
    [self.contentView addSubview:self.titleLabel];
    
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.titleLabel.superview attribute:NSLayoutAttributeLeading multiplier:1 constant:10];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.titleLabel.superview attribute:NSLayoutAttributeTop multiplier:1 constant:10];
    NSLayoutConstraint *width = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeWidth relatedBy:NSLayoutRelationEqual toItem:nil attribute:NSLayoutAttributeNotAnAttribute multiplier:1 constant:120];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.titleLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.titleLabel.superview attribute:NSLayoutAttributeBottom multiplier:1 constant:-10];
    self.titleLabel.translatesAutoresizingMaskIntoConstraints = NO;
    [self.contentView addConstraints:@[left, top, width, bottom]];
    
    self.titleLabelBottomCons = bottom;
}

#pragma mark - Getters and setters
- (void)setModel:(DoraemonHierarchyCellModel *)model {
    _model = model;
    self.titleLabel.text = model.title;
}

- (UILabel *)titleLabel {
    if (!_titleLabel) {
        _titleLabel = [[UILabel alloc] init];
        _titleLabel.font = [UIFont systemFontOfSize:16];
        _titleLabel.textColor = [UIColor doraemon_black_1];
    }
    return _titleLabel;
}

@end
