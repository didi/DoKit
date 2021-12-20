//
//  DoraemonWeexLogger.m
//  DoraemonKit
//
//  Created by yixiang on 2019/5/23.
//  Copyright © 2019年 Chameleon-Team. All rights reserved.
//

#import "DoraemonWeexLogger.h"
#import <WeexSDK/WeexSDK.h>
#import "DoraemonWeexLogModel.h"
#import "DoraemonWeexLogDataSource.h"

@interface DoraemonWeexLogger()<WXLogProtocol>


@end

@implementation DoraemonWeexLogger

- (void)startLog{
    [WXLog registerExternalLog:self];
}


- (WXLogLevel)logLevel{
    return WXLogLevelAll;
}

- (void)log:(WXLogFlag)flag message:(NSString *)message{
    if ([message containsString:@"jsLog:"]) {
        NSRange range = [message rangeOfString:@"jsLog:"];
        NSInteger jslogStart = range.location+range.length;
        if (message.length > jslogStart) {
            DoraemonWeexLogModel *model = [[DoraemonWeexLogModel alloc] init];
            model.flag = flag;
            model.timeInterval = [[NSDate date] timeIntervalSince1970];
            model.expand = YES;
            NSString *jsLog = [message substringFromIndex:(range.location+range.length)];
            model.content = jsLog;
            [[DoraemonWeexLogDataSource shareInstance] addLog:model];
        }
    }
}

@end
