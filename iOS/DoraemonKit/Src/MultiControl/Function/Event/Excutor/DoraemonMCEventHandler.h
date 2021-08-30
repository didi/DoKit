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

@property (nonatomic , strong) UIView *targetView;

- (BOOL)handleEvent:(DoraemonMCMessage*)eventInfo;

- (UIView *)fetchTargetView;

@end

@interface DoraemonMCGestureRecognizerEventHandler : DoraemonMCEventHandler


@end

@interface DoraemonMCControlEventHandler : DoraemonMCEventHandler


@end

@interface DoraemonMCReuseCellEventHandler : DoraemonMCEventHandler



@end

@interface DoraemonMCTextFiledEventHandler : DoraemonMCEventHandler


@end

@interface DoraemonMCTabbarEventHandler : DoraemonMCEventHandler


@end

NS_ASSUME_NONNULL_END
