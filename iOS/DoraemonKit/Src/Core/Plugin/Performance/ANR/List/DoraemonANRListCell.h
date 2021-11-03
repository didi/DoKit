//
//  DoraemonANRListCell.h
//  DoraemonKit
//
//  Created by yixiang on 2018/6/15.
//

#import <UIKit/UIKit.h>

@class DoraemonSandboxModel;

@interface DoraemonANRListCell : UITableViewCell

- (void)renderCellWithData:(DoraemonSandboxModel *)model;

+ (CGFloat)cellHeight;

@end
