//
//  DoraemonMCCommandGenerator.m
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import "DoraemonMCCommandGenerator.h"

@implementation DoraemonMCCommandGenerator

+ (DoraemonMCMessage *)sendMessageWithView:(UIView *)view
                                    gusture:(UIGestureRecognizer *)gusture
                                     action:(SEL)action
                                  indexPath:(NSIndexPath *)indexPath {
    DoraemonMCMessage *message = [[DoraemonMCMessage alloc] init];
    return message;
}
@end

