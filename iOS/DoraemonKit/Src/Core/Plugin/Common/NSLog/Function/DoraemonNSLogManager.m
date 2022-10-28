//
//  DoraemonNSLogManager.m
//  DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import "DoraemonNSLogManager.h"
#import "doraemon_fishhook.h"

//函数指针，用来保存原始的函数的地址
static void(*old_nslog)(NSString *format, ...);

//新的NSLog
void myNSLog(NSString *format, ...){
    
    va_list vl;
    va_start(vl, format);
    NSString* str = [[NSString alloc] initWithFormat:format arguments:vl];
    va_end(vl);
    
    [[DoraemonNSLogManager sharedInstance] addNSLog:str];
    //再调用原来的nslog
    //old_nslog(str);
    old_nslog(@"%@",str);
}

@interface DoraemonNSLogManager()
@property (nonatomic, strong) NSMutableArray<DoraemonNSLogModel *> *dataArray;
@property (nonatomic, strong) dispatch_queue_t logQueue;
@end

@implementation DoraemonNSLogManager

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (void)startNSLogMonitor{
    self.logQueue = dispatch_queue_create("dokit.log.queue", DISPATCH_QUEUE_SERIAL);
    doraemon_rebind_symbols((struct doraemon_rebinding[1]){"NSLog", (void *)myNSLog, (void **)&old_nslog},1);
}

- (void)stopNSLogMonitor{
    doraemon_rebind_symbols((struct doraemon_rebinding[1]){"NSLog", (void *)old_nslog, NULL},1);
}

- (void)addNSLog:(NSString *)log{
    dispatch_sync(self.logQueue, ^{
        DoraemonNSLogModel *model = [[DoraemonNSLogModel alloc] init];
        model.content = log;
        model.timeInterval = [[NSDate date] timeIntervalSince1970];
        
        if (!_dataArray) {
            _dataArray = [[NSMutableArray alloc] init];
        }
        [_dataArray addObject:model];
    });
    
//    return;
//    if (@available(iOS 13.0, *)) {
//    }else{
//        dispatch_async(dispatch_get_main_queue(), ^{
//            [[DoraemonStateBar shareInstance] renderUIWithContent:[NSString stringWithFormat:@"[NSLog] : %@",log] from:DoraemonStateBarFromNSLog];
//        });
//    }

}

- (NSArray<DoraemonNSLogModel *> *)readLogs {
    NSArray *array = [_dataArray copy];
    return array;
}

- (void)clearLogs {
    dispatch_sync(self.logQueue, ^{
        [self.dataArray removeAllObjects];
    });
}

@end
