//
//  DoraemonSandboxCell.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2017/12/11.
//

#import <UIKit/UIKit.h>
@class DoraemonSandboxModel;

@interface DoraemonSandBoxCell : UITableViewCell

- (void)renderUIWithData : (DoraemonSandboxModel *)model;

+ (CGFloat)cellHeight;

@end
