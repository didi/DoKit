//
//  DoraemonMultiCaseListViewController.h
//  DoraemonKit
//
//  Created by wzp on 2021/10/9.
//

#import "DoraemonBaseViewController.h"
#import "DoraemMultiCaseListModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMultiCaseListViewController : DoraemonBaseViewController

@end

@interface DoraemonCaseListCell : UITableViewCell

- (void)renderUIWithData:(DoraemMultiCaseListModel *)data;

@end

NS_ASSUME_NONNULL_END
