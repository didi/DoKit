//
//  DoraemonPingThread.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

// 参考ONEANRTracker

#import <Foundation/Foundation.h>

typedef void (^DoraemonANRTrackerBlock)(double threshold);

/**
 *  用于Ping主线程的线程类
 *  通过信号量控制来Ping主线程，判断主线程是否卡顿
 */
@interface DoraemonPingThread : NSThread

/**
 *  初始化Ping主线程的线程类
 *
 *  @param threshold 主线程卡顿阈值
 *  @param handler   监控到卡顿回调
 */
- (instancetype)initWithThreshold:(double)threshold
                          handler:(DoraemonANRTrackerBlock)handler;

@end
