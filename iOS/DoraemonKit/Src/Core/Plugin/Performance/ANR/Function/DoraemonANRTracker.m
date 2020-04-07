//
//  DoraemonANRTracker.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import "DoraemonANRTracker.h"
#import "sys/utsname.h"

/**
 *  主线程卡顿监控看门狗类
 */
@interface DoraemonANRTracker()

/**
 *  用于Ping主线程的线程实例
 */
@property (nonatomic, strong) DoraemonPingThread *pingThread;

@end

@implementation DoraemonANRTracker

- (instancetype)init
{
    self = [super init];
    if (self) {
    }
    return self;
}

- (void)startWithThreshold:(double)threshold
                   handler:(DoraemonANRTrackerBlock)handler {
    
    self.pingThread = [[DoraemonPingThread alloc] initWithThreshold:threshold
                                                            handler:^(NSDictionary *info) {
                                                                handler(info);
                                                            }];
    
    [self.pingThread start];
}

- (void)stop {
    if (self.pingThread != nil) {
        [self.pingThread cancel];
        self.pingThread = nil;
    }
}

- (DoraemonANRTrackerStatus)status {
    if (self.pingThread != nil && self.pingThread.isCancelled != YES) {
        return DoraemonANRTrackerStatusStart;
    }else {
        return DoraemonANRTrackerStatusStop;
    }
}

- (void)dealloc
{
    [self.pingThread cancel];
}

@end
