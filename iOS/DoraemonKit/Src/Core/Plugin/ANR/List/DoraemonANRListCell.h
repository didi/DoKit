//
//  DoraemonANRListCell.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import <UIKit/UIKit.h>

@interface DoraemonANRListCell : UITableViewCell

- (void)renderCellWithData:(NSDictionary *)dic;

+ (CGFloat)cellHeight;

@end
