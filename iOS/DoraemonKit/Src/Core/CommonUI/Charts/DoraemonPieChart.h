//
//  DoraemonPieChart.h
//  DoraemonKit
//
//  Created by 0xd on 2019/9/25.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonChart.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonPieChart : DoraemonChart
@property (nonatomic, strong) UIFont *itemDescriptionFont;
@property (nonatomic, strong) UIColor *itemDescriptionTextColor;

@property (nonatomic, assign) CGFloat innerCircleRadiusRatio;

@end

NS_ASSUME_NONNULL_END
