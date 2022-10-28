//
//  DoraemonNSLogManager.h
//  DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import <Foundation/Foundation.h>
#import "DoraemonNSLogModel.h"


@interface DoraemonNSLogManager : NSObject

+ (instancetype)sharedInstance;

- (void)startNSLogMonitor;

- (void)stopNSLogMonitor;

- (void)addNSLog:(NSString *)log;

- (NSArray<DoraemonNSLogModel *> *)readLogs;
- (void)clearLogs;

@end

