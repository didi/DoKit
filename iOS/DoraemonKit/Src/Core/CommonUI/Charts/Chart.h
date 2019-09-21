//
//  Chart.h
//  ccccc1111111
//
//  Created by 0xd on 2019/9/11.
//  Copyright © 2019 000. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ChartDataItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface Chart : UIView
@property (nonatomic, strong) UILabel *titleLabel;
@property (nonatomic, strong) NSNumberFormatter *vauleFormatter;
@property (nonatomic, copy) NSArray<ChartDataItem *> *items;
@property (nonatomic, assign) UIEdgeInsets contentInset;

- (void)itemsChanged;
@end

NS_ASSUME_NONNULL_END
