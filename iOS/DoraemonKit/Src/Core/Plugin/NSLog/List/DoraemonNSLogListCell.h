//
//  DoraemonNSLogListCell.h
//  AFNetworking
//
//  Created by yixiang on 2018/11/26.
//

#import <UIKit/UIKit.h>
#import "DoraemonNSLogModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonNSLogListCell : UITableViewCell

- (void)renderCellWithData:(DoraemonNSLogModel *)model;

+ (CGFloat)cellHeightWith:(nullable DoraemonNSLogModel *)model;

@end

NS_ASSUME_NONNULL_END
