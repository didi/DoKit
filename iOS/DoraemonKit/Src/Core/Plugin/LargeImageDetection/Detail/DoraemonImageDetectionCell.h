//
//  DoraemonImageDetectionCell.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/17.
//

#import <UIKit/UIKit.h>
@class DoraemonResponseImageModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonImageDetectionCell : UITableViewCell
+ (CGFloat)cellHeight;

- (void)setupWithModel:(DoraemonResponseImageModel *)model;
@end

NS_ASSUME_NONNULL_END
