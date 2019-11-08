//
//  DoraemonMockDetailCell.h
//  AFNetworking
//
//  Created by didi on 2019/10/24.
//

#import <UIKit/UIKit.h>
#import "DoraemonMockDetailModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockDetailCell : UITableViewCell

- (void)renderCellWithData:(DoraemonMockDetailModel *)model index:(NSInteger)index;

+ (CGFloat)cellHeightWith:(DoraemonMockDetailModel *)model;

@end

NS_ASSUME_NONNULL_END
