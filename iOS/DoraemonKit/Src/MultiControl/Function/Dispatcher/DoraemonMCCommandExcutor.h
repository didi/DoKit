//
//  DoraemonMCCommandExcutor.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import <Foundation/Foundation.h>
#import "DoraemonMCMessagePackager.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCCommandExcutor : NSObject

+ (void)excuteMessageStrFromNet:(NSString *)message;


+ (void)excuteMessage:(DoraemonMCMessage *)message;

@end

NS_ASSUME_NONNULL_END
