//
//  DoraemonANRManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import "DoraemonANRManager.h"
#import "DoraemonANRTracker.h"
#import <BSBacktraceLogger/BSBacktraceLogger.h>
#import "DoraemonUtil.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonAppInfoUtil.h"
#import "Doraemoni18NUtil.h"

//默认超时间隔
static int64_t const kDoraemonBlockMonitorTimeInterval = 2.;

@interface DoraemonANRManager()

@property (nonatomic, strong) DoraemonANRTracker *doraemonANRTracker;

@end

@implementation DoraemonANRManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init {
    self = [super init];
    
    if (self) {
        _doraemonANRTracker = [[DoraemonANRTracker alloc] init];
        _timeOut = kDoraemonBlockMonitorTimeInterval;
    }
    
    return self;
}

- (void)start {
    __weak typeof(self) weakSelf = self;
    [_doraemonANRTracker startWithThreshold:self.timeOut
                               handler:^(double threshold) {
                                   [weakSelf dump];
                               }];
}

- (void)dump {
    //方法一：使用 BSBacktraceLogger 打印方法调用栈
    //BSLOG  // 打印当前线程的调用栈
    //BSLOG_ALL  // 打印所有线程的调用栈
    //BSLOG_MAIN  // 打印主线程调用栈
    
    NSString *report = [BSBacktraceLogger bs_backtraceOfMainThread];
    if (!report) {
        report = DoraemonLocalizedString(@"空的report");
    }
    
    if (!_anrArray) {
        _anrArray = [NSMutableArray array];
    }
    NSDictionary *dic = @{
                          @"title":[DoraemonUtil dateFormatNow],
                          @"content":report
                          };
    [_anrArray addObject:dic];
}


- (void)dealloc {
    [self stop];
}

- (void)stop {
    [self.doraemonANRTracker stop];
}
@end
