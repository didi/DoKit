//
//  DoraemonMethodUseTimeListCell.h
//  DoraemonKit
//
//  Created by yixiang on 2019/1/23.
//

#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMethodUseTimeListCell : UITableViewCell

- (void)renderCellWithData:(NSDictionary *)dic;

+ (CGFloat)cellHeight;

@end

NS_ASSUME_NONNULL_END
