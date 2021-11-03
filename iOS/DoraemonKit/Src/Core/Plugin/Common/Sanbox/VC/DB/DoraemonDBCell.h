//
//  DoraemonDBCell.h
//  DoraemonKit
//
//  Created by yixiang on 2019/4/1.
//

#import <UIKit/UIKit.h>
#import "DoraemonDBRowView.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonDBCell : UITableViewCell

@property (nonatomic, strong) DoraemonDBRowView *rowView;

- (void)renderCellWithArray:(NSArray *)array;

@end

NS_ASSUME_NONNULL_END
