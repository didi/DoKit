//
//  DoraemonMockBaseListView.h
//  DoraemonKit
//
//  Created by didi on 2019/11/15.
//

#import <UIKit/UIKit.h>
#import "DoraemonMockBaseModel.h"
#import "DoraemonMockManager.h"
#import "DoraemonDefine.h"
#import "DoraemonMockBaseCell.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMockBaseListView : UIView<DoraemonMockBaseCellDelegate>

@property (nonatomic, copy) NSArray<DoraemonMockBaseModel *> *dataArray;
@property (nonatomic, strong) UITableView *tableView;

- (void)reloadUI;

@end

NS_ASSUME_NONNULL_END
