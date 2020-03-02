//
//  DoraemonNetFlowDataSource.m
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNetFlowDataSource.h"

@implementation DoraemonNetFlowDataSource{
    dispatch_semaphore_t semaphore;
}

+ (DoraemonNetFlowDataSource *)shareInstance{
    static dispatch_once_t once;
    static DoraemonNetFlowDataSource *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetFlowDataSource alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _httpModelArray = [NSMutableArray array];
        semaphore = dispatch_semaphore_create(1);
    }
    return self;
}

- (void)addHttpModel:(DoraemonNetFlowHttpModel *)httpModel{
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    [_httpModelArray insertObject:httpModel atIndex:0];
    dispatch_semaphore_signal(semaphore);
}

- (void)clear{
    dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    [_httpModelArray removeAllObjects];
    dispatch_semaphore_signal(semaphore);
}

@end
