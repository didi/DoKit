//
//  DoraemonMCEventHandler.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//


#import <Foundation/Foundation.h>
#import "DoraemonMCMessagePackager.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCEventHandler : NSObject

@property (nonatomic , strong) DoraemonMCMessage *messageInfo;

- (void)handleEvent:(DoraemonMCMessage*)eventInfo;

@end

@interface DoraemonMCGestureRecognizerEventHandler : DoraemonMCEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo;

@end

@interface DoraemonMCControlEventHandler : DoraemonMCEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo;

@end

@interface DoraemonMCTableViewEventHandler : DoraemonMCEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo;

@end

@interface DoraemonMCTextFiledEventHandler : DoraemonMCEventHandler

- (void)handleEvent:(DoraemonMCMessage*)eventInfo;

@end

NS_ASSUME_NONNULL_END
