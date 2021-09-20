//
//  DoraemonMCCommandExcutor.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import <Foundation/Foundation.h>
#import "DoraemonMCMessagePackager.h"
#import "DoraemonMCEventHandler.h"
NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCCommandExcutor : NSObject

+ (void)excuteMessageStrFromNet:(NSString *)message;


+ (void)excuteMessage:(DoraemonMCMessage *)message;


//增加自定义事件
+ (void)addCustomMessage:(NSString *)type eventHandlerName:(DoraemonMCEventHandler *)eventHandler;

@end

NS_ASSUME_NONNULL_END
