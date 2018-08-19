//
//  DoraemonLoggerConsoleViewController.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>
#import "DoraemonLoggerConsoleTipView.h"
#import "DoraemonLoggerConsoleSearchView.h"
#import "DoraemonLoggerConsoleSwitchView.h"
#import "DoraemonLoggerConsoleTableView.h"
#import "DoraemonLogger.h"

@protocol DoraemonLoggerConsoleViewControllerDelegate<NSObject>

- (void)toggleToMax;

- (void)toggleToMin;

@end

@interface DoraemonLoggerConsoleViewController : UIViewController

@property (nonatomic, weak) id<DoraemonLoggerConsoleViewControllerDelegate> delegate;

@property (nonatomic, strong) DoraemonLoggerConsoleTipView *tipView;
@property (nonatomic, strong) DoraemonLoggerConsoleSearchView *searchView;
@property (nonatomic, strong) DoraemonLoggerConsoleSwitchView *switchView;
@property (nonatomic, strong) DoraemonLoggerConsoleTableView *tableView;
@property (nonatomic, strong) DoraemonLogger *logger;

@end
