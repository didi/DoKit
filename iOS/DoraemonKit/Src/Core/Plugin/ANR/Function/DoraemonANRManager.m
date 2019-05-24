//
//  DoraemonANRManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/6/14.
//

#import "DoraemonANRManager.h"
#import "DoraemonANRTracker.h"
#import "DoraemonMemoryUtil.h"
#import "DoraemonAppInfoUtil.h"
#import "Doraemoni18NUtil.h"

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

- (void)start {
    __weak typeof(self) weakSelf = self;
    [_doraemonANRTracker startWithThreshold:self.timeOut handler:^(NSDictionary *info) {
        __strong typeof(weakSelf) strongSelf = weakSelf;
        [strongSelf dumpWithInfo:info];
    }];
}

- (void)dumpWithInfo:(NSDictionary *)info {
    if (![info isKindOfClass:[NSDictionary class]]) {
        return;
    }
    if (self.block) {
        self.block(info);
    }
    if (!_anrArray) {
        _anrArray = [NSMutableArray array];
    }
    [_anrArray addObject:info];
}

- (void)addANRBlock:(DoraemonANRManagerBlock)block{
    self.block = block;
}


- (void)dealloc {
    [self stop];
}

- (void)stop {
    [self.doraemonANRTracker stop];
}
@end
