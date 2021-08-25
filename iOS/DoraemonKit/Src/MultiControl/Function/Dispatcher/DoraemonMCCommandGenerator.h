//
//  DoraemonMCCommandGenerator.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import <UIKit/UIKit.h>
#import "DoraemonMCMessagePackager.h"



@interface DoraemonMCCommandGenerator : NSObject

+ (void )sendMessageWithView:(UIView *)view
                                    gusture:(UIGestureRecognizer *)gusture
                                     action:(SEL)action
                                  indexPath:(NSIndexPath *)indexPath
                               messageType:(DoraemonMCMessageType)type;

+ (DoraemonMCMessage *)sendCustomMessageWithView:(UIView *)view
                                       eventInfo:(NSDictionary *)eventInfo
                                     messageType:(NSString *)type;



@end


