//
//  DoraemonMCCommandGenerator.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMCCommandGenerator.h"
#import "DoraemonMCMessagePackager.h"
#import "DoraemonMCServer.h"

@implementation DoraemonMCCommandGenerator

+ (void)sendMessageWithView:(UIView *)view
                                   gusture:(UIGestureRecognizer *)gusture
                                    action:(SEL)action
                                 indexPath:(NSIndexPath *)indexPath
                               messageType:(DoraemonMCMessageType)type {
    @autoreleasepool {
        DoraemonMCMessage *message = [DoraemonMCMessagePackager packageMessageWithView:view
                                                                               gusture:gusture
                                                                                action:action
                                                                             indexPath:indexPath
                                                                           messageType:type];
        if (message) {
            [DoraemonMCServer sendMessage:message.toMessageString];
        }
        message = nil;
    }

}
@end

