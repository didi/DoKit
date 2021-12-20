//
//  DoraemonHierarchyDetailTitleCell.h
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonHierarchyTitleCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyDetailTitleCell : DoraemonHierarchyTitleCell

@property (nonatomic, strong, readonly) UILabel *detailLabel;

@property (nonatomic, strong, readonly) NSLayoutConstraint *detailLabelRightCons;

@end

NS_ASSUME_NONNULL_END
