//
//  ChartDataItem.m
//  oxCharts
//
//  Created by 0xd on 2019/9/9.
//  Copyright © 2019 000. All rights reserved.
//

#import "ChartDataItem.h"

@implementation ChartDataItem

- (instancetype)initWithValue:(double)value
                  name:(NSString *)name
                        color:(UIColor *)color {
    if (self = [super init]) {
        self.value = value;
        self.name = name;
        self.color = color;
    }
    return self;
}

@end
