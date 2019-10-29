//
//  DoraemonPingThread.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import "DoraemonPingThread.h"
#import <UIKit/UIKit.h>
#import "BSBacktraceLogger.h"
#import "DoraemonUtil.h"

@interface DoraemonPingThread()

/**
 *  应用是否在活跃状态
 */
@property (nonatomic, assign) BOOL isApplicationInActive;

/**
 *  控制ping主线程的信号量
 */
@property (nonatomic, strong) dispatch_semaphore_t semaphore;

/**
 *  卡顿阈值
 */
@property (nonatomic, assign) double threshold;

/**
 *  卡顿回调
 */
@property (nonatomic, copy) DoraemonANRTrackerBlock handler;

/**
 *  主线程是否阻塞
 */
@property (nonatomic, assign,getter=isMainThreadBlock) BOOL mainThreadBlock;
/**
 *  判断是否需要上报
 */
@property (nonatomic, copy) NSString *reportInfo;
/**
 *  每一次ping开始的时间,上报延迟时间统计
 */
@property (nonatomic, assign) double startTimeValue;
@end

@implementation DoraemonPingThread

- (instancetype)initWithThreshold:(double)threshold
                          handler:(DoraemonANRTrackerBlock)handler {
    self = [super init];
    if (self) {
        self.semaphore = dispatch_semaphore_create(0);
        
        self.threshold = threshold;
        self.handler = handler;
        _isApplicationInActive = YES;
        
        [[NSNotificationCenter defaultCenter] addObserver: self selector: @selector(applicationDidBecomeActive) name: UIApplicationDidBecomeActiveNotification object: nil];
        [[NSNotificationCenter defaultCenter] addObserver: self selector: @selector(applicationDidEnterBackground) name: UIApplicationDidEnterBackgroundNotification object: nil];
    }
    return self;
}

- (void)dealloc {
    [[NSNotificationCenter defaultCenter] removeObserver: self];
}

- (void)main {
    //判断是否需要上报
    __weak typeof(self) weakSelf = self;
    void (^ verifyReport)(void) = ^() {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        if (strongSelf.reportInfo.length > 0) {
            if (strongSelf.handler) {
                double responseTimeValue = floor([[NSDate date] timeIntervalSince1970] * 1000);
                double duration = (responseTimeValue - strongSelf.startTimeValue) / 1000.0;
                strongSelf.handler(@{
                                     @"title": [DoraemonUtil dateFormatNow].length > 0 ? [DoraemonUtil dateFormatNow] : @"",
                                     @"duration": [NSString stringWithFormat:@"%.2f",duration],
                                     @"content": strongSelf.reportInfo
                                     });
            }
            strongSelf.reportInfo = @"";
        }
    };
    
    while (!self.cancelled) {
        if (_isApplicationInActive) {
            self.mainThreadBlock = YES;
            self.reportInfo = @"";
            self.startTimeValue = floor([[NSDate date] timeIntervalSince1970] * 1000);
            dispatch_async(dispatch_get_main_queue(), ^{
                self.mainThreadBlock = NO;
                verifyReport();
                dispatch_semaphore_signal(self.semaphore);
            });
            [NSThread sleepForTimeInterval:self.threshold];
            if (self.isMainThreadBlock) {
                self.reportInfo = [BSBacktraceLogger bs_backtraceOfMainThread];
            }
            dispatch_semaphore_wait(self.semaphore, dispatch_time(DISPATCH_TIME_NOW, 5.0 * NSEC_PER_SEC));
            {
                //卡顿超时情况;
                verifyReport();
            }
        } else {
            [NSThread sleepForTimeInterval:self.threshold];
        }
    }
}

#pragma mark - Notification
- (void)applicationDidBecomeActive {
    _isApplicationInActive = YES;
}

- (void)applicationDidEnterBackground {
    _isApplicationInActive = NO;
}

@end
