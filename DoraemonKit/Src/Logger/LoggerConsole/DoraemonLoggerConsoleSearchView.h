//
//  DoraemonLoggerConsoleSearchView.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>

@protocol DoraemonLoggerConsoleSearchViewDelegate<NSObject>

- (void)searchViewCurrentText:(NSString *)text;

@end

@interface DoraemonLoggerConsoleSearchView : UIView

@property (nonatomic, weak) id<DoraemonLoggerConsoleSearchViewDelegate> delegate;

- (void)clearSearchView;

@end
