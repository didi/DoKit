//
//  DoraemonNetFlowListCell.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/13.
//

#import <UIKit/UIKit.h>
#import "DoraemonNetFlowHttpModel.h"

@interface DoraemonNetFlowListCell : UITableViewCell

- (void)renderCellWithModel:(DoraemonNetFlowHttpModel *)httpModel;

+ (CGFloat)cellHeightWithModel:(DoraemonNetFlowHttpModel *)httpModel;

@end
