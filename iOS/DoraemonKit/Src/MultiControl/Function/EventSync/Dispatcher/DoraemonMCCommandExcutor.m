//
//  DoraemonMCCommandExcutor.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMCCommandExcutor.h"
#import "DoraemonMCEventHandler.h"

static NSMutableDictionary *eventHandlerMap = nil;
static NSMutableDictionary *externalEventHandlerMap = nil;

@implementation DoraemonMCCommandExcutor

+ (void)excuteMessageStrFromNet:(NSString *)message {
    DoraemonMCMessage *messageInstance = [DoraemonMCMessagePackager parseMessageString:message];
    [self excuteMessage:messageInstance];
    
}

+ (void)excuteMessage:(DoraemonMCMessage *)message {
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        DoraemonMCReuseCellEventHandler *handlerReuseCell =  [DoraemonMCReuseCellEventHandler new];
        eventHandlerMap = @{
            @(DoraemonMCMessageTypeControl): [DoraemonMCControlEventHandler new],
            @(DoraemonMCMessageTypeDidSelectCell) : handlerReuseCell,
            @(DoraemonMCMessageTypeDidScrollToCell) : handlerReuseCell,
            @(DoraemonMCMessageTypeGuesture) : [DoraemonMCGestureRecognizerEventHandler new],
            @(DoraemonMCMessageTypeTextInput) : [DoraemonMCTextFiledEventHandler new],
            @(DoraemonMCMessageTypeTarbarSelected) : [DoraemonMCTabbarEventHandler new]
        };
        externalEventHandlerMap = [NSMutableDictionary new];
    });

    [eventHandlerMap enumerateKeysAndObjectsUsingBlock:^(NSNumber * _Nonnull typeNumber, DoraemonMCEventHandler * _Nonnull eventHandler, BOOL * _Nonnull stop) {
        if (message.type  == typeNumber.intValue) {
            [eventHandler handleEvent:message];
            *stop = YES;
        }
    }];
    
    
    [externalEventHandlerMap enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull typeString, DoraemonMCEventHandler * _Nonnull eventHandler, BOOL * _Nonnull stop) {
        if ([message.customType isEqualToString:typeString]) {
            [eventHandler handleEvent:message];
            *stop = YES;
        }
    }];

    
}

//增加自定义事件
+ (void)addCustomMessage:(NSString *)type eventHandlerName:(DoraemonMCEventHandler *)eventHandler {
    if (eventHandler && type) {
        
        [externalEventHandlerMap enumerateKeysAndObjectsUsingBlock:^(NSString * _Nonnull typeString, DoraemonMCEventHandler * _Nonnull eventHandler, BOOL * _Nonnull stop) {
            if ([type isEqualToString:typeString]) {
                *stop = YES;
                NSAssert(stop, @"重复添加事件");
            }
        }];
        
        [externalEventHandlerMap setValue:eventHandler forKey:type];
    }
}





@end
