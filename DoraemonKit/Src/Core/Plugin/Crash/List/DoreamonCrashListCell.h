//
//  DoreamonCrashListCell.h
//  DoraemonKit
//
//  Created by wenquan on 2018/11/22.
//

#import <UIKit/UIKit.h>

@class DoraemonSandboxModel;

NS_ASSUME_NONNULL_BEGIN

@interface DoreamonCrashListCell : UITableViewCell

- (void)renderUIWithData:(DoraemonSandboxModel *)model;

+ (CGFloat)cellHeight;

@end

NS_ASSUME_NONNULL_END
