//
//  DoraemonMCCommandExcutor.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMCCommandExcutor.h"
#import "DoraemonMCEventHandler.h"

@implementation DoraemonMCCommandExcutor

+ (void)excuteMessageStrFromNet:(NSString *)message {
    DoraemonMCMessage *messageInstance = [DoraemonMCMessagePackager parseMessageString:message];
    [self excuteMessage:messageInstance];
    
}

+ (void)excuteMessage:(DoraemonMCMessage *)message {
    static NSDictionary *eventHandlerMap = nil ;
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
    });

    [eventHandlerMap enumerateKeysAndObjectsUsingBlock:^(NSNumber * _Nonnull typeNumber, DoraemonMCEventHandler * _Nonnull eventHandler, BOOL * _Nonnull stop) {
        if (message.type  == typeNumber.intValue) {
            [eventHandler handleEvent:message];
            *stop = YES;
        }
    }];
    
}

@end
