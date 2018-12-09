//
//  DoraemonPerformanceInfoModel.m
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import "DoraemonPerformanceInfoModel.h"

@implementation DoraemonPerformanceInfoModel

- (NSDictionary *)dictionary {
    return @{@"time": @(self.time), @"value":@(self.value), @"heightValue":@(self.heightValue)};
}

@end
