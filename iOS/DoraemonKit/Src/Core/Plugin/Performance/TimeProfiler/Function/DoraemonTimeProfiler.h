//
//  DoraemonTimeProfiler.h
//  DoraemonKit
//
//  Created by yixiang on 2019/7/10.
//

#import <Foundation/Foundation.h>
#import "DoraemonTimeProfilerRecord.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonTimeProfiler : NSObject

+ (instancetype)sharedInstance;
@property (nonatomic, assign) BOOL on;

/// 开始记录
+ (void)startRecord;

/// 停止记录
+ (void)stopRecord;

/// 清空已有记录
+ (void)clearRecords;

/// 分享调用记录
+ (void)shareRecords;

/// 打印调用记录
+ (void)printRecords;

/// 获取调用记录
+ (NSArray<DoraemonTimeProfilerRecord *> *)getRecords;

#pragma mark - 配置项
/// 设置过滤的最小函数调用时间（毫秒），小于该时间的函数调用将被忽略。 默认值:1.0
+ (void)setMinCallCost:(double)ms;

/// 设置过滤的最深函数调用，调用深度超过该值的函数将被忽略。 默认值:10
+ (void)setMaxCallDepth:(int)depth;

@end

NS_ASSUME_NONNULL_END
