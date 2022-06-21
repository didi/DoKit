//
//  DoraemonMCServer.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCServer.h"
#import <DoraemonKit/DKMultiControlStreamManager.h>

@implementation DoraemonMCServer

+ (void)sendMessage:(NSString *)message {
    [DKMultiControlStreamManager.sharedInstance broadcastWithActionMessage:message];
}


+ (BOOL)isOpen {
    return DKMultiControlStreamManager.sharedInstance.state == DKMultiControlStreamManagerStateMaster;
}

@end
