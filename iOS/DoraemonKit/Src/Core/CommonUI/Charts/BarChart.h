//
//  BarChart.h
//  ccccc1111111
//
//  Created by 0xd on 2019/9/11.
//  Copyright © 2019 000. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Chart.h"
#import "XAxis.h"
#import "YAxis.h"
#import "ChartDataItem.h"

NS_ASSUME_NONNULL_BEGIN

@interface BarChart : Chart
@property (nonatomic, strong) XAxis *xAxis;
@property (nonatomic, strong) YAxis *yAxis;
@property (nonatomic, assign) CGFloat barsSpacingRatio;

@end

NS_ASSUME_NONNULL_END
