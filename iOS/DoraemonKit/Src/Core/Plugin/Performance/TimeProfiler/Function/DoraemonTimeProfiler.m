//
//  DoraemonTimeProfiler.m
//  DoraemonKit
//
//  Created by yixiang on 2019/7/10.
//

#import "DoraemonTimeProfiler.h"
#import "DoraemonTimeProfilerCore.h"
#include <objc/message.h>
#include <sys/sysctl.h>
#import "DoraemonHealthManager.h"
#import "DoraemonDefine.h"

static NSTimeInterval startTime;
static NSTimeInterval stopTime;

@implementation DoraemonTimeProfiler

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    if (self = [super init]) {
        _on = NO;
    }
    return self;
}

- (void)setOn:(BOOL)on{
    if (on) {
        [[self class] startRecord];
    }else{
        [[self class] stopRecord];
    }
}


+ (void)startRecord {
    startTime = [NSDate new].timeIntervalSince1970;
    dtp_hook_begin();
}

+ (void)stopRecord {
    dtp_hook_end();
    stopTime = [NSDate new].timeIntervalSince1970;
    [self printRecords];
    [self clearRecords];
}

+ (void)clearRecords {
    dtp_clear_call_records();
}

+ (void)shareRecords {
    NSString *result = [self getRecordsResult];
    [self share:result];
}

/// 打印调用记录
+ (void)printRecords {
    NSString *result = [self getRecordsResult];
    [DoraemonHealthManager sharedInstance].costDetail = result;
    NSLog(@"%@",result);
}

+ (NSString *)getRecordsResult {
    NSMutableString *str = [NSMutableString new];
    [str appendFormat:@"\n\ntime profile result : \n"];
    NSTimeInterval totalRecord = 0;
    NSArray<DoraemonTimeProfilerRecord *>*arr = [self getRecords];
    for (DoraemonTimeProfilerRecord *r in arr) {
        [self appendRecord:r to:str];
        totalRecord += r.timeCost;
    }
    
    [str appendFormat:@"\n"];
    [str appendFormat:@"totalTimeInRecord:%.2f\n",totalRecord * 1000];
    
    [str appendFormat:@"\ntime profile end\n"];
    
    return str;
}


+ (void)appendRecord:(DoraemonTimeProfilerRecord *)record to:(NSMutableString *)str {
    [str appendFormat:@"%@\n", [record descriptionWithDepth]];
    for (DoraemonTimeProfilerRecord *r in record.subRecords) {
        [self appendRecord:r to:str];
    }
}

+ (NSArray<DoraemonTimeProfilerRecord *>*)getRecords {
    NSMutableArray<DoraemonTimeProfilerRecord *> *arr = [NSMutableArray new];
    int record_num = 0;
    dtp_call_record *records = dtp_get_call_records(&record_num);
    for (int i = 0; i < record_num; i++) {
        dtp_call_record *rec = &records[i];
        
        DoraemonTimeProfilerRecord *r = [DoraemonTimeProfilerRecord new];
        r.className = NSStringFromClass(rec->cls);
        r.methodName = NSStringFromSelector(rec->sel);
        r.isClassMethod = class_isMetaClass(rec->cls);
        r.timeCost = (double)rec->time / 1000000.0;
        r.callDepth = rec->depth;
        [arr addObject:r];
    }
    
    for (int i = 0, max = (int)arr.count; i < max; i++) {
        DoraemonTimeProfilerRecord *r = arr[i];
        if (r.callDepth > 0) {
            [arr removeObjectAtIndex:i];
            for (int j = i; j < max - 1; j++) {
                if (arr[j].callDepth + 1 == r.callDepth) {
                    NSMutableArray *sub = (NSMutableArray *)arr[j].subRecords;
                    if (!sub) {
                        sub = [NSMutableArray new];
                        arr[j].subRecords = sub;
                    }
                    [sub insertObject:r atIndex:0];
                    break;
                }
            }
            i--; max--;
        }
    }
    return arr;
}

+ (void)setMinCallCost:(double)ms {
    dtp_set_min_time(ms * 1000);
}

+ (void)setMaxCallDepth:(int)depth {
    if (depth < 0) depth = 0;
    dtp_set_max_depth(depth);
}

+ (void)share:(NSString *)str {
}

@end
