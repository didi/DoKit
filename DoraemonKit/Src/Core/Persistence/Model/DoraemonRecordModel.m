//
//  DoraemonRecordModel.m
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import "DoraemonRecordModel.h"

@implementation DoraemonRecordModel

+ (instancetype)instanceWithType:(DoraemonRecordType)type {
    DoraemonRecordModel *instance = [[DoraemonRecordModel alloc] init];
    instance.type = type;
    return instance;
}

- (void)addRecordValue:(CGFloat)value heightValue:(CGFloat)heightValue time:(NSTimeInterval)time {
    if (!self.recordsArray) {
        self.recordsArray = [NSMutableArray array];
    }
    
    DoraemonPerformanceInfoModel *record = [[DoraemonPerformanceInfoModel alloc] init];
    record.value = value;
    record.heightValue = heightValue;
    record.time = time;
    [self.recordsArray addObject:record];
}

@end

