//
//  DoraemonMockBaseCell.h
//  DoraemonKit
//
//  Created by didi on 2019/11/15.
//

#import <UIKit/UIKit.h>
#import "DoraemonMockBaseModel.h"
#import "DoraemonDefine.h"
#import "DoraemonMockDetailSwitch.h"
#import "DoraemonMockUpLoadModel.h"

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonMockBaseCellDelegate<NSObject>

@optional
- (void)cellExpandClick;
- (void)sceneBtnClick;
- (void)cellSwitchClick;
- (void)previewClick:(DoraemonMockUpLoadModel *)uploadModel;

@end

@interface DoraemonMockBaseCell : UITableViewCell

@property (nonatomic, weak) id<DoraemonMockBaseCellDelegate> delegate;
@property (nonatomic, strong) DoraemonMockBaseModel *model;
@property (nonatomic, strong) DoraemonMockDetailSwitch *detailSwitch;
@property (nonatomic, strong) UILabel *infoLabel;

- (void)renderCellWithData:(DoraemonMockBaseModel *)model;

+ (CGFloat)cellHeightWith:(DoraemonMockBaseModel *)model;


@end

NS_ASSUME_NONNULL_END
