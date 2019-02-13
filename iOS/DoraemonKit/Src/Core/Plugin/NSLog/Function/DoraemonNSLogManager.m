//
//  DoraemonNSLogManager.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/11/26.
//

#import "DoraemonNSLogManager.h"
#import <fishhook/fishhook.h>
#import "DoraemonStateBar.h"

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
    //定义rebinding结构体
    struct rebinding nslogBind;
    //函数名称
    nslogBind.name = "NSLog";
    //新的函数地址
    nslogBind.replacement = myNSLog;
    //保存原始函数地址的变量的指针
    nslogBind.replaced = (void *)&old_nslog;
    
    //数组
    struct rebinding rebs[]={nslogBind};
    
    /*
     arg1:存放rebinding结构体的数组
     arg2:数组的长度
     */
    rebind_symbols(rebs, 1);
}

- (void)addNSLog:(NSString *)log{
    DoraemonNSLogModel *model = [[DoraemonNSLogModel alloc] init];
    model.content = log;
    model.timeInterval = [[NSDate date] timeIntervalSince1970];
    
    if (!_dataArray) {
        _dataArray = [[NSMutableArray alloc] init];
    }
    [_dataArray addObject:model];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [[DoraemonStateBar shareInstance] renderUIWithContent:[NSString stringWithFormat:@"[NSLog] : %@",log] from:DoraemonStateBarFromNSLog];
    });

}

@end
