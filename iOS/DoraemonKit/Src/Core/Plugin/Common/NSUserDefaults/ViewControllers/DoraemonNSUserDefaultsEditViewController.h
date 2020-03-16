//
//  DoraemonNSUserDefaultsEditViewController.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/11/26.
//

#import <DoraemonKit/DoraemonKit.h>
#import "DoraemonBaseViewController.h"
@class DoraemonNSUserDefaultsModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonNSUserDefaultsEditViewController : DoraemonBaseViewController
@property (nonatomic, strong) DoraemonNSUserDefaultsModel *model;

- (instancetype)initWithModel: (DoraemonNSUserDefaultsModel *)model;
@end

NS_ASSUME_NONNULL_END
