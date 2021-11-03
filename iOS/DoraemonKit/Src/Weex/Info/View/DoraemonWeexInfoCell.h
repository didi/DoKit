//
//  DoraemonWeexInfoCell.h
//  DoraemonKit
//
//  Created by yixiang on 2019/6/5.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonWeexInfoCell : UITableViewCell

- (void)renderCellWithTitle:(NSString *)title content:(NSString *)content;

+ (CGFloat)cellHeight;

@end

NS_ASSUME_NONNULL_END
