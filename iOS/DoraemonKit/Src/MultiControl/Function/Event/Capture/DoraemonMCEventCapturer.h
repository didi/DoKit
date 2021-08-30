//
//  DoraemonMCEventCapturer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <UIKit/UIKit.h>
#import "DoraemonMCGestureTargetActionPair.h"

@interface UIGestureRecognizer (DoraemonMCSupport)

- (void)do_mc_handleGestureSend:(id)sender;

@end

@interface UIApplication (DoraemonMCSupport)

@end


@interface UIControl (DoraemonMCSupport)

@end

