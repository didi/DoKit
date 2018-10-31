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

//默认超时间隔
static int64_t const kDoraemonBlockMonitorTimeInterval = 1.;

@interface DoraemonANRManager()

@property (nonatomic, strong) DoraemonANRTracker *doraemonANRTracker;

@property (nonatomic, copy) DoraemonANRManagerBlock block;

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

- (void)addAnrBlock:(DoraemonANRManagerBlock)block{
    self.block = block;
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
        report = @"空的report";
    }
    
    if (!_anrArray) {
        _anrArray = [NSMutableArray array];
    }
    NSDictionary *dic = @{
                          @"title":[DoraemonUtil dateFormatNow],
                          @"content":report
                          };
    [_anrArray addObject:dic];
    
    [self commitToServerWithReport:report];
    
    
    // 方法二：使用 PLCrashReporter 打印方法调用栈
//    PLCrashReporterConfig *config = [[PLCrashReporterConfig alloc] initWithSignalHandlerType:PLCrashReporterSignalHandlerTypeBSD
//                                                                       symbolicationStrategy:PLCrashReporterSymbolicationStrategyAll];
//    PLCrashReporter *crashReporter = [[PLCrashReporter alloc] initWithConfiguration:config];
//    NSData *data = [crashReporter generateLiveReportWithThread:[NSThread mainThread]];
//    PLCrashReport *reporter = [[PLCrashReport alloc] initWithData:data error:NULL];
//    NSString *report = [PLCrashReportTextFormatter stringValueForCrashReport:reporter
//                                                              withTextFormat:PLCrashReportTextFormatiOS];
//    NSLog(@"------------\n%@\n------------", report);
}

- (void)commitToServerWithReport:(NSString *)report{
    if(!report) return;
    NSString *anrTime = [DoraemonUtil dateFormatNow];
    NSString *phoneName = [DoraemonAppInfoUtil iphoneType];
    NSString *phoneSystem = [[UIDevice currentDevice] systemVersion];
    NSUInteger totalMemory = [DoraemonMemoryUtil totalMemoryForDevice];
    NSUInteger phoneMemory = totalMemory;//MB为单位
    NSString *appVersion = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleVersion"];
    NSString *appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleDisplayName"];
    if (!appName) {
        appName = [[[NSBundle mainBundle] infoDictionary] objectForKey:@"CFBundleName"];
    }
    
    NSDictionary *upLoadData = @{
                                 @"testTime":anrTime,
                                 @"phoneName":phoneName,
                                 @"phoneSystem":phoneSystem,
                                 @"phoneMemory":@(phoneMemory),
                                 @"appVersion":appVersion,
                                 @"appName":appName,
                                 @"report":report
                                 };
    if (self.block) {
        self.block(upLoadData);
    }
}


- (void)dealloc {
    [self stop];
}

- (void)stop {
    [self.doraemonANRTracker stop];
}
@end
