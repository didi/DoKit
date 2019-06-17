//
//  DoraemonWeexLogCell.h
//  Chameleon_Example
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonWeexLogModel.h"


@interface DoraemonWeexLogCell : UITableViewCell

- (void)renderCellWithData:(DoraemonWeexLogModel *)model;

+ (CGFloat)cellHeightWith:(DoraemonWeexLogModel *)model;

@end

