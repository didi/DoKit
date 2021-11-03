//
//  DoraemonWeexLogDataSource.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import "DoraemonWeexLogDataSource.h"
#import "DoraemonWeexLogger.h"

@interface DoraemonWeexLogDataSource()

@property (nonatomic, strong) DoraemonWeexLogger *weexLogger;

@end

@implementation DoraemonWeexLogDataSource

+ (nonnull DoraemonWeexLogDataSource *)shareInstance{
    static dispatch_once_t once;
    static DoraemonWeexLogDataSource *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonWeexLogDataSource alloc] init];
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
        _logs = [[NSMutableArray alloc] init];
        _weexLogger = [[DoraemonWeexLogger alloc] init];
        [_weexLogger startLog];
    }
    return self;
}

- (void)addLog:(DoraemonWeexLogModel *)model{
    if (_logs.count > 500) {
        [_logs removeLastObject];
    }
    [_logs insertObject:model atIndex:0];
}


@end
