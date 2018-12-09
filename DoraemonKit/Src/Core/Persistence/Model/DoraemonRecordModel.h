//
//  DoraemonRecordModel.h
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import <Foundation/Foundation.h>
#import "DoraemonPerformanceInfoModel.h"

NS_ASSUME_NONNULL_BEGIN

typedef NS_ENUM(NSUInteger, DoraemonRecordType) {
    DoraemonRecordTypeNone = 0,
    DoraemonRecordTypeFPS,
    DoraemonRecordTypeCPU,
    DoraemonRecordTypeMemory,
    DoraemonRecordTypeNetFlow
};

@interface DoraemonRecordModel : NSObject

@property (nonatomic, assign) DoraemonRecordType type;

@property (nonatomic, assign) long long startTime;

@property (nonatomic, assign) long long endTime;

@property (nonatomic, strong) NSMutableArray<DoraemonPerformanceInfoModel *> *recordsArray;

+ (instancetype)instanceWithType:(DoraemonRecordType)type;

- (void)addRecordValue:(CGFloat)value heightValue:(CGFloat)heightValue time:(NSTimeInterval)time;

@end

NS_ASSUME_NONNULL_END
