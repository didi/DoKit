//
//  DoraemonMessagePackager.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCMessagePackager.h"

@implementation DoraemonMCMessagePackager

/**
 根据类型,xPath和事件信息组装消息字符串,用于发送到网络
 */
+ (NSString *)packageMessageWithType:(DoraemonMCMessageType)type xPath:(NSString *)xPath eventInfo:(NSDictionary *)eventInfo {
    DoraemonMCMessage *messageInstance = [[DoraemonMCMessage alloc] init];
    messageInstance.type = type;
    messageInstance.xPath = xPath;
    messageInstance.eventInfo = eventInfo;
    return [messageInstance toMessageString];
}

/***
 根据从网络上获取的消息字符串, 解析出消息对象
 */
+ (DoraemonMCMessage *)parseMessageString:(NSString *)messageString {
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:[messageString dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:NULL];
    DoraemonMCMessage *messageInstance = [[DoraemonMCMessage alloc] init];
    messageInstance.type = [dict[@"type"] integerValue];
    messageInstance.xPath = dict[@"xPath"];
    messageInstance.eventInfo = dict[@"eventInfo"];
    return messageInstance;
}

@end

@implementation DoraemonMCMessage

- (NSString *)toMessageString {
    NSDictionary *dict = @{
        @"type" : @(self.type),
        @"xPath" : self.xPath?:@"",
        @"eventInfo" : self.eventInfo?:@{}
    };
    if ([NSJSONSerialization isValidJSONObject:dict]) {
        return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:NULL] encoding:NSUTF8StringEncoding];
    }
    return nil;
}

@end
