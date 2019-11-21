//
//  Axis.m
//  Chart
//
//  Created by 0xd on 2019/9/4.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonChartAxis.h"
#import <CoreGraphics/CoreGraphics.h>

@implementation DoraemonChartAxis

- (instancetype)init
{
    self = [super init];
    if (self) {
        self.labelFont = [UIFont systemFontOfSize:13];
        self.axisLineColor = [UIColor grayColor];
        self.axisLineWidth = 1;
        self.axisLabelFont = [UIFont systemFontOfSize:13];
        self.axisLabelTextColor = [UIColor grayColor];
        self.vauleFormatter = [[NSNumberFormatter alloc] init];
    }
    return self;
}

- (NSNumberFormatter *)vauleFormatter {
    if (!_vauleFormatter) {
        _vauleFormatter = [[NSNumberFormatter alloc] init];
        _vauleFormatter.maximumFractionDigits = 2;
    }
    return _vauleFormatter;
}

@end
