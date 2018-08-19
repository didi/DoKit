//
//  DoraemonLoggerConsoleTableView.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleTableView.h"
#import "DoraemonLogger.h"

@implementation DoraemonLoggerConsoleTableView

- (instancetype)initWithFrame:(CGRect)frame style:(UITableViewStyle)style{
    self = [super initWithFrame:frame style:style];
    if (self) {
        [self commonInit];
    }
    return self;
}

- (void)commonInit{
    self.rowHeight = 20.;
    self.logger = [[DoraemonLogger alloc] init];
    //self.logger.tableView = self;
}

- (void)setLogger:(DoraemonLogger *)logger{
    _logger = logger;
    self.dataSource = _logger;
    self.delegate = _logger;
}

@end
