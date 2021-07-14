//
//  DoraemonMCCommandGenerator.h
//  DoraemonKit-DoraemonKit
//
//  Created by litianhao on 2021/7/12.
//

#import <UIKit/UIKit.h>
#import "DoraemonMCMessagePackager.h"

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCCommandGenerator : NSObject

+ (DoraemonMCMessage *)sendMessageWithView:(UIView *)view
                                    gusture:(UIGestureRecognizer *)gusture
                                     action:(SEL)action
                                  indexPath:(NSIndexPath *)indexPath;

@end

NS_ASSUME_NONNULL_END
