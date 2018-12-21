//
//  DoraemonPingThread.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import "DoraemonPingThread.h"
#import <UIKit/UIKit.h>

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
    
    while (!self.cancelled) {
        if (_isApplicationInActive) {
            self.mainThreadBlock = YES;
            
            dispatch_async(dispatch_get_main_queue(), ^{
                self.mainThreadBlock = NO;
                dispatch_semaphore_signal(self.semaphore);
            });
            
            [NSThread sleepForTimeInterval:self.threshold];
            
            if (self.isMainThreadBlock) {
                self.handler(self.threshold);
            }
            
            dispatch_semaphore_wait(self.semaphore, DISPATCH_TIME_FOREVER);
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
