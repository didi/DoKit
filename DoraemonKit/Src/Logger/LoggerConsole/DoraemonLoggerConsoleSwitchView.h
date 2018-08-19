//
//  DoraemonLoggerConsoleSwitchView.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>

@protocol DoraemonLoggerConsoleSwitchViewDelegate<NSObject>

- (void)segmentSelected:(NSInteger)index;

@end

@interface DoraemonLoggerConsoleSwitchView : UIView

@property (nonatomic, weak) id<DoraemonLoggerConsoleSwitchViewDelegate> delegate;

@end
