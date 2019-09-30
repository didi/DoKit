//
//  Chart.h
//  DoraemonKit
//
//  Created by 0xd on 2019/9/11.
//  Copyright © 2019 000. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "DoraemonChartDataItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonChart : UIView
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) NSNumberFormatter *vauleFormatter;
@property (nonatomic, copy) NSArray<DoraemonChartDataItem *> *items;
@property (nonatomic, assign) UIEdgeInsets contentInset;

- (void)display;
@end

NS_ASSUME_NONNULL_END
