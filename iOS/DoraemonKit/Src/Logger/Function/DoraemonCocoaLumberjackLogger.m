//
//  DoraemonCocoaLumberjackLogger.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/12/5.
//

#import "DoraemonCocoaLumberjackLogger.h"
#import <CocoaLumberjack/CocoaLumberjack.h>
#import "DoraemonDDLogMessage.h"
#import "DoraemonStatebar.h"

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
        [DDLog addLogger:self];
    }
    return self;
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
        
        dispatch_async(dispatch_get_main_queue(), ^{
            [[DoraemonStateBar shareInstance] renderUIWithContent:[NSString stringWithFormat:@"[Lumberjack] : %@",logMessage.message] from:DoraemonStateBarFromCocoaLumberjack];
        });
    });
}

@end
