//
//  DoraemonPersistenceUtil.h
//  DoraemonKit-DoraemonKit
//
//  Created by ZhangHonglin on 2018/11/7.
//

#import <Foundation/Foundation.h>
#import "DoraemonRecordModel.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonPersistenceUtil : NSObject

+ (void)saveRecord:(DoraemonRecordModel *)record;

+ (NSArray *)getLocalRecordsWithType:(DoraemonRecordType)type;

+ (NSArray *)getRecordDetailWithType:(DoraemonRecordType)type fileName:(NSString *)fileName;

@end

NS_ASSUME_NONNULL_END
