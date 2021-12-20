//
//  DoraemonCocoaLumberjackLogger.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/5.
//

#import <CocoaLumberjack/DDLog.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonCocoaLumberjackLogger : DDAbstractLogger

+ (instancetype)sharedInstance;

- (void)startMonitor;

- (void)stopMonitor;

@property (nonatomic, strong) dispatch_queue_t consoleQueue;
@property (nonatomic, strong) NSMutableArray *messages; 

@end

NS_ASSUME_NONNULL_END
