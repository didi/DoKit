//
//  DoraemonMessagePackager.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCMessagePackager.h"
#import "DoraemonMCXPathSerializer.h"
#import "DoraemonMCGustureSerializer.h"

static NSString const *kIsFirstResponderKey = @"isFirstResponder";
static NSString const *kVcClsNameKey = @"vcClsName";
static NSString const *kEventInfoKey = @"eventInfo";
static NSString const *kXpathKey = @"xPath";
static NSString const *kTypeKey = @"type";



@implementation DoraemonMCMessagePackager

/**
 根据类型,xPath和事件信息组装消息字符串,用于发送到网络
 */
+ (DoraemonMCMessage *)packageMessageWithView:(UIView *)view
                             gusture:(UIGestureRecognizer *)gusture
                              action:(SEL)action
                           indexPath:(NSIndexPath *)indexPath
                         messageType:(DoraemonMCMessageType)type {
    DoraemonMCMessage *messageInstance = [[DoraemonMCMessage alloc] init];
    messageInstance.type = type;
    messageInstance.xPath = [DoraemonMCXPathSerializer xPathStringWithView:view];
    switch (type) {
        case DoraemonMCMessageTypeControl:
        {
            messageInstance.eventInfo =  @{
                @"action": NSStringFromSelector(action)?:@"",
            };
            break;
        }
        case DoraemonMCMessageTypeGuesture:
        {
            messageInstance.eventInfo =  [DoraemonMCGustureSerializer dictFromGusture:gusture];
            break;
        }
        case DoraemonMCMessageTypeDidSelectCell:
        {
            messageInstance.eventInfo =  @{
                @"section": @(indexPath.section),
                @"row": @(indexPath.row)
            };
            break;
        }
        case DoraemonMCMessageTypeTextInput:
        {
            NSString *text = nil;
            if ([view respondsToSelector:@selector(text)]) {
                text = [view valueForKey:@"text"];
            }
            NSMutableDictionary *dictM = [NSMutableDictionary dictionaryWithDictionary:@{
                @"section": @(indexPath.section),
                @"row": @(indexPath.row) ,
            }];
            if (text) {
                dictM[@"text"] = text;
            }
            messageInstance.eventInfo =  dictM.copy;
            break;
        }
        default:
            break;
    }
    messageInstance.isFirstResponder = view.isFirstResponder;
    UIViewController *vc = [DoraemonMCXPathSerializer ownerVCWithView:view];
    if (vc) {
        messageInstance.currentVCClassName = NSStringFromClass(vc.class) ;
    }
    return messageInstance;
}


/***
 根据从网络上获取的消息字符串, 解析出消息对象
 */
+ (DoraemonMCMessage *)parseMessageString:(NSString *)messageString {
    NSDictionary *dict = [NSJSONSerialization JSONObjectWithData:[messageString dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingAllowFragments error:NULL];
    DoraemonMCMessage *messageInstance = [[DoraemonMCMessage alloc] init];
    messageInstance.type = [dict[kTypeKey] integerValue];
    messageInstance.xPath = dict[kXpathKey];
    messageInstance.eventInfo = dict[kEventInfoKey];
    messageInstance.currentVCClassName = dict[kVcClsNameKey];
    messageInstance.isFirstResponder = dict[kIsFirstResponderKey];
    return messageInstance;
}

@end

@implementation DoraemonMCMessage

- (NSString *)toMessageString {
    NSDictionary *dict = @{
        kTypeKey : @(self.type),
        kXpathKey : self.xPath?:@"",
        kEventInfoKey : self.eventInfo?:@{},
        kVcClsNameKey: self.currentVCClassName?:@"",
        kIsFirstResponderKey : @(self.isFirstResponder)
    };
    if ([NSJSONSerialization isValidJSONObject:dict]) {
        return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:NULL] encoding:NSUTF8StringEncoding];
    }
    return nil;
}

@end
