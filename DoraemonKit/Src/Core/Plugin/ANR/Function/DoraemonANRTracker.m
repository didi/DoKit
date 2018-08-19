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
        self.filterBelowiPhone4s = YES;
    }
    return self;
}

- (void)startWithThreshold:(double)threshold
                   handler:(DoraemonANRTrackerBlock)handler {
    //过滤4S以下机型
    if ([self belowiPhone4s] && self.filterBelowiPhone4s) {
        return;
    }
    
    self.pingThread = [[DoraemonPingThread alloc] initWithThreshold:threshold
                                                       handler:^(double threshold) {
                                                           handler(threshold);
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

- (BOOL)belowiPhone4s {
    struct utsname systemInfo;
    uname(&systemInfo);
    NSString *deviceString = [NSString stringWithCString:systemInfo.machine
                                                encoding:NSUTF8StringEncoding];
    if ([deviceString isEqualToString:@"iPhone1,1"]         //"iPhone 1G"
        ||[deviceString isEqualToString:@"iPhone1,2"]       //"iPhone 3G"
        ||[deviceString isEqualToString:@"iPhone2,1"]       //"iPhone 3GS"
        ||[deviceString isEqualToString:@"iPhone3,1"]       //"iPhone 4"
        ||[deviceString isEqualToString:@"iPhone4,1"]       //"iPhone 4S"
        ) {
        return YES;
        
    }else {
        return NO;
    }
}

- (void)dealloc
{
    [self.pingThread cancel];
}

@end
