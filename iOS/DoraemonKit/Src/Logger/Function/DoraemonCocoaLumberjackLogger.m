//
//  DoraemonCocoaLumberjackLogger.m
//  DoraemonKit
//
//  Created by yixiang on 2018/12/5.
//

#import "DoraemonCocoaLumberjackLogger.h"
#import <CocoaLumberjack/CocoaLumberjack.h>
#import "DoraemonDDLogMessage.h"

@implementation DoraemonCocoaLumberjackLogger

+ (instancetype)sharedInstance {
    static id instance = nil;
    static dispatch_once_t onceToken;
    
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

- (instancetype)init{
    self = [super init];
    if (self) {
        _consoleQueue = dispatch_queue_create("console_queue", NULL);
        _messages = NSMutableArray.array;
    }
    return self;
}

- (void)startMonitor{
    [DDLog addLogger:self];
}

- (void)stopMonitor{
    [DDLog removeLogger:self];
}

- (void)logMessage:(DDLogMessage *)logMessage{
    DoraemonDDLogMessage *message = [[DoraemonDDLogMessage alloc] init];
    message.timestamp = logMessage.timestamp;
    message.flag = logMessage.flag;
    message.message = logMessage.message;
    message.fileName = logMessage.fileName;
    message.line = logMessage.line;
    message.threadId = logMessage.threadID;
    message.threadName = logMessage.threadName;
    message.expand = NO;
    
    __weak typeof(self) weakSelf = self;
    dispatch_async(_consoleQueue, ^{
        [weakSelf.messages insertObject:message atIndex:0];
    });
}

@end
