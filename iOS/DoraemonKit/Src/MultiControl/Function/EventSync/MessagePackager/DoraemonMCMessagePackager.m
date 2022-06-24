//
//  DoraemonMessagePackager.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/13.
//

#import "DoraemonMCMessagePackager.h"
#import "DoraemonMCXPathSerializer.h"
#import "DoraemonMCGustureSerializer.h"
#import "UIResponder+DoraemonMCSerializer.h"

static NSString const *kIsFirstResponderKey = @"isFirstResponder";
static NSString const *kVcClsNameKey = @"vcClsName";
static NSString const *kEventInfoKey = @"eventInfo";
static NSString const *kXpathKey = @"xPath";
static NSString const *kTypeKey = @"type";
static NSString const *kcustomTypeKey =@"customType";
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
    DoraemonMCXPathSerializer *xPathInstance = [DoraemonMCXPathSerializer xPathInstanceWithView:view];
    if (xPathInstance.windowIndex == NSNotFound) {
        // 如果存在埋点 SDK，埋点会被后调用，先前调用的业务 action 关闭了当前页面，导致 sender 不存在于视图中，会出现这种情况。这种情况需要过滤
        return nil;
    }
    if (xPathInstance.ignore) {
        return nil;
    }
    // tabbar上的控件 使用hook系统tabbar点击的方式同步， 只处理DoraemonMCMessageTypeTarbarSelected的情况
    if ([xPathInstance.vcCls isKindOfClass:[UITabBarController class]] && type != DoraemonMCMessageTypeTarbarSelected) {
        return nil;
    }
    messageInstance.xPath = [xPathInstance generalPathToTransfer];
    messageInstance.isFirstResponder = view.isFirstResponder;
    UIViewController *vc = [DoraemonMCXPathSerializer ownerVCWithView:view];
    if (vc) {
        messageInstance.currentVCClassName = NSStringFromClass(vc.class) ;
    }
    
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
        case DoraemonMCMessageTypeDidScrollToCell:
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
            NSMutableDictionary *dictM = [NSMutableDictionary dictionary];
            if (text) {
                dictM[@"text"] = text;
            }
            [dictM addEntriesFromDictionary:[view do_mc_serialize_dictionary]];
            messageInstance.eventInfo =  dictM.copy;
            break;
        }
        case DoraemonMCMessageTypeTarbarSelected:
        {
            if ([vc isKindOfClass:[UITabBarController class]]) {
                UITabBarController *tabbarC = (UITabBarController *)vc;
                messageInstance.eventInfo = @{
                    @"selectIndex" : @(indexPath.row),
                    @"selectVC" : NSStringFromClass(tabbarC.selectedViewController.class)
                };
            }else {
                messageInstance = nil;
            }
            break;
        }
        default:
            break;
    }

    return messageInstance;
}


/*
 * 自定义事件
 */
+ (DoraemonMCMessage *)packageCustomMessageWithView:(UIView *)view
                                          eventInfo:(NSDictionary *)eventInfo
                                        messageType:(NSString *)type {
    DoraemonMCMessage *messageInstance = [[DoraemonMCMessage alloc] init];
    messageInstance.customType = type;
    messageInstance.xPath = [DoraemonMCXPathSerializer xPathStringWithView:view];
    messageInstance.eventInfo = eventInfo;
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
    messageInstance.customType = dict[kcustomTypeKey];
    messageInstance.xPath = dict[kXpathKey];
    messageInstance.eventInfo = dict[kEventInfoKey];
    messageInstance.currentVCClassName = dict[kVcClsNameKey];
    messageInstance.isFirstResponder = [dict[kIsFirstResponderKey] boolValue];
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
    if (self.customType.length) {
        dict = @{
            kcustomTypeKey : self.customType,
            kXpathKey : self.xPath?:@"",
            kEventInfoKey : self.eventInfo?:@{},
            kVcClsNameKey: self.currentVCClassName?:@"",
            kIsFirstResponderKey : @(self.isFirstResponder)
        };
    }
    if ([NSJSONSerialization isValidJSONObject:dict]) {
        return [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:dict options:NSJSONWritingPrettyPrinted error:NULL] encoding:NSUTF8StringEncoding];
    }
    return nil;
}

@end
