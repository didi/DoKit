//
//  DoraemonYAxis.h
//  DoraemonKit
//
//  Created by 0xd on 2019/9/6.
//  Copyright © 2019 000. All rights reserved.
//

#import "DoraemonChartAxis.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonYAxis : DoraemonChartAxis
@property (nonatomic, assign) CGFloat marginTop;
@property (nonatomic, assign) CGFloat labelWidth;
@property (nonatomic, assign) NSInteger labelCount;
@property (nonatomic, assign) double maxY;
@property (nonatomic, copy) NSArray<NSString *> *labels;
@property (nonatomic, copy) NSArray<NSNumber *> *values;
- (void)update;
@end

NS_ASSUME_NONNULL_END
