//
//  DoraemonMCViewController.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonBaseViewController.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSInteger , DoraemonMCViewControllerType) {
    DoraemonMCViewControllerTypeServer,
    DoraemonMCViewControllerTypeClient
};

@interface DoraemonMCViewController : DoraemonBaseViewController

+ (instancetype)instanceWithType:(DoraemonMCViewControllerType)type;

@end

NS_ASSUME_NONNULL_END
