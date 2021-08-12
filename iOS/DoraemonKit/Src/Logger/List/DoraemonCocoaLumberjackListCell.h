//
//  DoraemonCocoaLumberjackListCell.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/6.
//

#import <UIKit/UIKit.h>
#import "DoraemonDDLogMessage.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonCocoaLumberjackListCell : UITableViewCell

- (void)renderCellWithData:(DoraemonDDLogMessage *)model;

+ (CGFloat)cellHeightWith:(nullable DoraemonDDLogMessage *)model;

@end

NS_ASSUME_NONNULL_END
