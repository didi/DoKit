//
//  DoraemonWeexLogCell.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonWeexLogModel.h"

NS_ASSUME_NONNULL_BEGIN
@interface DoraemonWeexLogCell : UITableViewCell

- (void)renderCellWithData:(DoraemonWeexLogModel *)model;

+ (CGFloat)cellHeightWith:(nullable DoraemonWeexLogModel *)model;

@end
NS_ASSUME_NONNULL_END

