//
//  DoraemonWeexStorageCell.h
//  DoraemonKit
//
//  Created by yixiang on 2019/5/30.
//  Copyright © 2019年 taobao. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonWeexStorageRowView.h"

@interface DoraemonWeexStorageCell : UITableViewCell

@property (nonatomic, strong) DoraemonWeexStorageRowView *rowView;

- (void)renderCellWithArray:(NSArray *)array;

@end

