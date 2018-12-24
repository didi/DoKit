//
//  DoraemonAppInfoCell.h
//  Aspects
//
//  Created by yixiang on 2018/4/14.
//

#import <UIKit/UIKit.h>

@interface DoraemonAppInfoCell : UITableViewCell

- (void)renderUIWithData:(NSDictionary *)data;

+ (CGFloat)cellHeight;

@end
