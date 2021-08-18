//
//  DoraemonMCViewController.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonBaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger , DoraemonMCViewControllerWorkMode) {
    DoraemonMCViewControllerWorkModeNone,
    DoraemonMCViewControllerWorkModeServer,
    DoraemonMCViewControllerWorkModeClient
};

@interface DoraemonMCViewController : DoraemonBaseViewController

@end

NS_ASSUME_NONNULL_END
