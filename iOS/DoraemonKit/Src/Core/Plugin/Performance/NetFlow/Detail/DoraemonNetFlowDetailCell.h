//
//  DoraemonNetFlowDetailCell.h
//  DoraemonKit
//
//  Created by yixiang on 2018/4/19.
//

#import <UIKit/UIKit.h>

@interface DoraemonNetFlowDetailCell : UITableViewCell

- (void)renderUIWithContent:(NSString *)content isFirst:(BOOL)isFirst isLast:(BOOL)isLast;
+ (CGFloat)cellHeightWithContent:(NSString *)content;

@end
