//
//  DoraemonMockDetailCell.h
//  AFNetworking
//
//  Created by didi on 2019/10/24.
//

#import <UIKit/UIKit.h>
#import "DoraemonMockAPI.h"

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonMockDetailCellDelegate<NSObject>

- (void)cellExpandClick;
- (void)sceneBtnClick;
- (void)cellSwitchClick;

@end

@interface DoraemonMockDetailCell : UITableViewCell

@property (nonatomic, weak) id<DoraemonMockDetailCellDelegate> delegate;

- (void)renderCellWithData:(DoraemonMockAPI *)model;

+ (CGFloat)cellHeightWith:(DoraemonMockAPI *)model;

@end

NS_ASSUME_NONNULL_END
