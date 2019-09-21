//
//  YAxis.m
//  oxCharts
//
//  Created by 0xd on 2019/9/6.
//  Copyright © 2019 000. All rights reserved.
//

#import "YAxis.h"

@interface YAxis()

@end

@implementation YAxis

- (instancetype)init
{
    self = [super init];
    if (self) {
        _labelCount = 5;
        _maxY = 1;
        _minY = 0;
        _marginTop = 20;
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
    double range = maxValueY;
    double rawInterval = range / (self.labelCount - 1);
    double interval =  roundedToNextSignficant(rawInterval);
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
