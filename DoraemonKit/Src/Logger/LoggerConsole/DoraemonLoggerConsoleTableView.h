//
//  DoraemonLoggerConsoleTableView.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <UIKit/UIKit.h>
@class DoraemonLogger;

@interface DoraemonLoggerConsoleTableView : UITableView

@property (nonatomic, strong) DoraemonLogger *logger;
@property (nonatomic, strong) UISearchBar *searchBar;
-(void)clearConsole;

@end
