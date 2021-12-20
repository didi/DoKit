//
//  DoraemonHierarchyDetailTitleCell.m
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyDetailTitleCell.h"
#import "DoraemonHierarchyCellModel.h"
#import "DoraemonDefine.h"

@interface DoraemonHierarchyDetailTitleCell ()

@property (nonatomic, strong) UILabel *detailLabel;

@property (nonatomic, strong) NSLayoutConstraint *detailLabelRightCons;

@end

@implementation DoraemonHierarchyDetailTitleCell

#pragma mark - Over write
- (void)initUI {
    [super initUI];
    
    [self.contentView addSubview:self.detailLabel];
    
    [self.contentView removeConstraint:self.titleLabelBottomCons];
    
    NSLayoutConstraint *right = [NSLayoutConstraint constraintWithItem:self.detailLabel attribute:NSLayoutAttributeTrailing relatedBy:NSLayoutRelationEqual toItem:self.detailLabel.superview attribute:NSLayoutAttributeTrailing multiplier:1 constant:-10];
    NSLayoutConstraint *top = [NSLayoutConstraint constraintWithItem:self.detailLabel attribute:NSLayoutAttributeTop relatedBy:NSLayoutRelationEqual toItem:self.detailLabel.superview attribute:NSLayoutAttributeTop multiplier:1 constant:10];
    NSLayoutConstraint *bottom = [NSLayoutConstraint constraintWithItem:self.detailLabel attribute:NSLayoutAttributeBottom relatedBy:NSLayoutRelationEqual toItem:self.detailLabel.superview attribute:NSLayoutAttributeBottom multiplier:1 constant:-10];
    NSLayoutConstraint *left = [NSLayoutConstraint constraintWithItem:self.detailLabel attribute:NSLayoutAttributeLeading relatedBy:NSLayoutRelationEqual toItem:self.titleLabel attribute:NSLayoutAttributeTrailing multiplier:1 constant:10 / 2.0];
    self.detailLabel.translatesAutoresizingMaskIntoConstraints = NO;
    [self.contentView addConstraints:@[right, top, bottom, left]];
    
    self.detailLabelRightCons = right;
}

#pragma mark - Getters and setters
- (void)setModel:(DoraemonHierarchyCellModel *)model {
    [super setModel:model];
    if (model.detailTitle == nil || model.detailTitle.length == 0) {
        self.detailLabel.text = @" ";
    } else {
        self.detailLabel.text = model.detailTitle;
    }
}

- (UILabel *)detailLabel {
    if (!_detailLabel) {
        _detailLabel = [[UILabel alloc] init];
        _detailLabel.font = [UIFont systemFontOfSize:14];
        _detailLabel.textColor = [UIColor doraemon_black_1];
        _detailLabel.textAlignment = NSTextAlignmentRight;
        _detailLabel.numberOfLines = 0;
    }
    return _detailLabel;
}

@end
