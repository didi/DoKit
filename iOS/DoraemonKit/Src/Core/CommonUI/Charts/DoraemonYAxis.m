//
//  YAxis.m
//  oxCharts
//
//  Created by 0xd on 2019/9/6.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonYAxis.h"

@interface DoraemonYAxis()

@end

@implementation DoraemonYAxis

- (instancetype)init
{
    self = [super init];
    if (self) {
        _labelCount = 5;
        _maxY = 1;
        _marginTop = 10;
        _labels = [NSArray array];
    }
    return self;
}

- (void)update {
    if (self.values.count == 0) {
        return;
    }
    double maxValueY = DBL_MIN;
    for (NSNumber *value in self.values) {
        maxValueY = MAX([value doubleValue], maxValueY);
    }
    // 防止出现y轴最大值小于label个数
    double range = MAX(maxValueY, self.labelCount - 1);
    double rawInterval = range / (self.labelCount - 1);
    double interval = roundedToNextSignficant(rawInterval);
    while (interval * (self.labelCount  - 1) < range) {
        rawInterval = rawInterval * 1.1;
        interval = roundedToNextSignficant(rawInterval);
    }
    self.maxY = interval * (self.labelCount  - 1);

    NSMutableArray *labels = [NSMutableArray arrayWithCapacity:self.labelCount];
    for (int i = 0; i < self.labelCount; i++) {
        NSString *label = [self.vauleFormatter stringFromNumber: [[NSNumber alloc] initWithDouble:interval * i]];
        [labels addObject:label];
    }
    self.labels = [labels copy];
}

double roundedToNextSignficant(double number) {
    if (number == 0) { return number; }
    NSInteger d = (NSInteger)ceil(log10(number < 0 ? -number : number));
    NSInteger pw = 1 - d;
    double magnitude = pow(10.0, (double)pw);
    double shifted = roundf(number * magnitude);
    return shifted / magnitude;
}

@end
