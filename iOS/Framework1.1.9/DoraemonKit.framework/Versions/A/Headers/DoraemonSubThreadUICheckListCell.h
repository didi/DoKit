//
//  DoraemonSubThreadUICheckListCell.h
//  AFNetworking
//
//  Created by yixiang on 2018/9/13.
//

#import <UIKit/UIKit.h>

@interface DoraemonSubThreadUICheckListCell : UITableViewCell

- (void)renderCellWithData:(NSDictionary *)dic;

+ (CGFloat)cellHeight;

@end
