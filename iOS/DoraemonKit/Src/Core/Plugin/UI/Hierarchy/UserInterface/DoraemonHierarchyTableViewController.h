//
//  DoraemonHierarchyTableViewController.h
//  DoraemonKit
//
//  Created by lijiahuan on 2019/11/2.
//

#import "DoraemonBaseViewController.h"

@class DoraemonHierarchyCategoryModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonHierarchyTableViewController : DoraemonBaseViewController <UITableViewDelegate, UITableViewDataSource>

@property (nonatomic, strong, readonly) UITableView *tableView;

@property (nonatomic, strong, readonly) NSMutableArray <DoraemonHierarchyCategoryModel *>*dataArray;

@end

NS_ASSUME_NONNULL_END
