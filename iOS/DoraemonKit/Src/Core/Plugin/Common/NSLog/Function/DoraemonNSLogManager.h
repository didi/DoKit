//
//  DoraemonNSLogManager.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import <Foundation/Foundation.h>
#import "DoraemonNSLogModel.h"


@interface DoraemonNSLogManager : NSObject

+ (instancetype)sharedInstance;

@property (nonatomic, strong) NSMutableArray<DoraemonNSLogModel *> *dataArray;

- (void)startNSLogMonitor;

- (void)stopNSLogMonitor;

- (void)addNSLog:(NSString *)log;
@end
