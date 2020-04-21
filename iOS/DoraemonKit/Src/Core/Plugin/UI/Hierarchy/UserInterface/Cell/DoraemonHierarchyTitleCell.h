//
//  DoraemonHierarchyTitleCell.h
//  DoraemonKit-DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import <UIKit/UIKit.h>

@class DoraemonHierarchyCellModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyTitleCell : UITableViewCell

@property (nonatomic, strong, readonly) UILabel *titleLabel;

@property (nonatomic, strong, readonly) NSLayoutConstraint *titleLabelBottomCons;

@property (nonatomic, strong) DoraemonHierarchyCellModel *model;

- (void)initUI;

@end

NS_ASSUME_NONNULL_END
