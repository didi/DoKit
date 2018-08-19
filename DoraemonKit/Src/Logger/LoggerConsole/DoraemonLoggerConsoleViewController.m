//
//  DoraemonLoggerConsoleViewController.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLoggerConsoleViewController.h"
#import "UIImage+DoraemonKit.h"
#import <UIView+Positioning/UIView+Positioning.h>
#import "DoraemonDefine.h"


@interface DoraemonLoggerConsoleViewController ()<DoraemonLoggerDelegate,UITableViewDelegate,UITableViewDataSource,DoraemonLoggerConsoleSwitchViewDelegate,DoraemonLoggerConsoleSearchViewDelegate>

//最大最小化按钮
@property (nonatomic, strong) UIButton *maxOrMinBtn;
@property (nonatomic, assign) BOOL fullScreen;

@end

@implementation DoraemonLoggerConsoleViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    self.view.backgroundColor = [UIColor clearColor];
    
    self.logger.delegate = self;
    
    _maxOrMinBtn = [[UIButton alloc] initWithFrame:CGRectMake(self.view.right - 20, IPHONE_TOPSENSOR_HEIGHT, 20, 20)];
    [_maxOrMinBtn setImage:[UIImage doraemon_imageNamed:@"maximize"] forState:UIControlStateNormal];
    [_maxOrMinBtn addTarget:self action:@selector(toggleFullScreen:) forControlEvents:UIControlEventTouchUpInside];
    [self.view addSubview:_maxOrMinBtn];
    self.fullScreen = NO;


    _tipView = [[DoraemonLoggerConsoleTipView alloc] initWithFrame:CGRectMake(0, IPHONE_TOPSENSOR_HEIGHT, self.view.width, 20)];
    [self.view addSubview:_tipView];

    _searchView = [[DoraemonLoggerConsoleSearchView alloc] initWithFrame:CGRectMake(0, _tipView.bottom, self.view.width, 40)];
    _searchView.delegate = self;
    [self.view addSubview:_searchView];

    _switchView = [[DoraemonLoggerConsoleSwitchView alloc] initWithFrame:CGRectMake(0, _searchView.bottom, self.view.width, 40)];
    _switchView.delegate = self;
    [self.view addSubview:_switchView];

    _tableView = [[DoraemonLoggerConsoleTableView alloc] initWithFrame:CGRectMake(0, _switchView.bottom, self.view.width, self.view.height-_switchView.bottom)];
    _tableView.backgroundColor = [UIColor clearColor];
    _tableView.delegate = self;
    _tableView.dataSource = self;
    _tableView.separatorStyle = UITableViewCellSeparatorStyleNone;
    [self.view addSubview:_tableView];

    [self.view bringSubviewToFront:_maxOrMinBtn];
}

- (void)toggleFullScreen : (UIButton *)btn{
    self.fullScreen = !self.fullScreen;
    if (self.fullScreen) {
        [_maxOrMinBtn setImage:[UIImage doraemon_imageNamed:@"minimize"] forState:UIControlStateNormal];
        if (self.delegate && [self.delegate respondsToSelector:@selector(toggleToMax)]) {
            [self.delegate toggleToMax];
        }
    }else{
        [_maxOrMinBtn setImage:[UIImage doraemon_imageNamed:@"maximize"] forState:UIControlStateNormal];
        if (self.delegate && [self.delegate respondsToSelector:@selector(toggleToMin)]) {
            [self.delegate toggleToMin];
        }
    }
}


#pragma mark - DoraemonLoggerDelegate
-(void)addNewLog:(DDLogMessage *)logMessage{
    if(_tipView && !_tipView.hidden){
        [_tipView showCurrentLog:[self textForCellWithLogMessage:logMessage]];
        [_tipView showCurrentLogColor:[self textColor:logMessage]];
    }
}

-(void)loggerReloadData{
    NSLog(@"loggerReloadData");
    [self.tableView reloadData];
}

-(void)loggerUpdateTableViewRowsRemoving:(NSInteger)itemsToRemoveCount
                               inserting:(NSInteger)itemsToInsertCount{
    NSLog(@"loggerUpdateTableViewRows");
    [self updateTableViewRowsRemoving:itemsToRemoveCount inserting:itemsToInsertCount];
}

- (void)updateTableViewRowsRemoving:(NSInteger)itemsToRemoveCount
                          inserting:(NSInteger)itemsToInsertCount
{
    // Remove paths
    NSMutableArray * removePaths = [NSMutableArray arrayWithCapacity:itemsToRemoveCount];
    if(itemsToRemoveCount > 0){
        NSUInteger tableCount = [self.tableView numberOfRowsInSection:0];
        for (NSInteger i = tableCount - itemsToRemoveCount; i < tableCount; i++){
            [removePaths addObject:[NSIndexPath indexPathForRow:i
                                                      inSection:0]];
        }
    }

    // Insert paths
    NSMutableArray * insertPaths = [NSMutableArray arrayWithCapacity:itemsToInsertCount];
    for (NSInteger i = 0; i < itemsToInsertCount; i++){
        [insertPaths addObject:[NSIndexPath indexPathForRow:i
                                                  inSection:0]];
    }

    // Update table view, we should never crash
    @try{
        [self.tableView beginUpdates];
        if (itemsToRemoveCount > 0){
            [self.tableView deleteRowsAtIndexPaths:removePaths
                                  withRowAnimation:UITableViewRowAnimationFade];
        }
        if (itemsToInsertCount > 0){
            [self.tableView insertRowsAtIndexPaths:insertPaths
                                  withRowAnimation:UITableViewRowAnimationFade];
        }
        [self.tableView endUpdates];
    }
    @catch (NSException * exception){
        [self.tableView reloadData];
    }
}

#pragma mark - DoraemonLoggerConsoleSwitchViewDelegate
- (void)segmentSelected:(NSInteger)index{
    switch (index) {
        case 0: _logger.currentLogLevel = DDLogLevelVerbose; break;
        case 1: _logger.currentLogLevel = DDLogLevelDebug; break;
        case 2: _logger.currentLogLevel = DDLogLevelInfo; break;
        case 3: _logger.currentLogLevel = DDLogLevelWarning; break;
        case 4: _logger.currentLogLevel = DDLogLevelError; break;
        default: _logger.currentLogLevel = DDLogLevelVerbose; break;
    }
    
    [_logger updateTableViewInConsoleQueue];
}

#pragma mark - DoraemonLoggerConsoleSearchViewDelegate
- (void)searchViewCurrentText:(NSString *)text{
    _logger.currentSearchText = text;
    [_logger updateTableViewInConsoleQueue];
}

#pragma mark - Table delagate and datasource
- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section{
    NSInteger row = (_logger.filteringEnabled ? _logger.filteredMessages : _logger.messages).count;
    return row;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath{
    // 收起cell
    DDLogMessage * logMessage = (_logger.filteringEnabled ? _logger.filteredMessages : _logger.messages)[indexPath.row];
    if (![_logger.expandedMessages containsObject:logMessage]){
        return 20.0;
    }

    // 展开cell
    NSString * string = [self textForCellWithLogMessage:logMessage];
    CGSize size;
    // Save a sample label reference
    static UILabel * labelModel;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^
                  {
                      labelModel = [self labelForNewCell];
                  });

    labelModel.text = string;
    size = [labelModel textRectForBounds:CGRectMake(0.0, 0.0,
                                                    tableView.bounds.size.width,
                                                    CGFLOAT_MAX)
                  limitedToNumberOfLines:0].size;

    return size.height + 20.0;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath{
    DDLogMessage * logMessage = (_logger.filteringEnabled ? _logger.filteredMessages : _logger.messages)[indexPath.row];
    // Load cell
    NSString * identifier = @"logMessage";
    UITableViewCell * cell = [tableView dequeueReusableCellWithIdentifier:identifier];
    UILabel * label;
    if (!cell){
        cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault
                                      reuseIdentifier:identifier];
        cell.clipsToBounds = YES;
        cell.backgroundColor = UIColor.clearColor;
        cell.selectionStyle = UITableViewCellSelectionStyleNone;
        label = [self labelForNewCell];
        label.frame = cell.contentView.bounds;
        [cell.contentView addSubview:label];
    }
    else{
        label = (UILabel *)cell.contentView.subviews[0];
    }

    // Configure the label
    label.textColor = [self textColor:logMessage];
    label.text = [self textForCellWithLogMessage:logMessage];

    return cell;
}

- (UIColor *)textColor:(DDLogMessage *)logMessage{
    UIColor *color;
    switch (logMessage->_flag){
        case DDLogFlagError   : color = [UIColor redColor];       break;
        case DDLogFlagWarning : color = [UIColor orangeColor];    break;
        case DDLogFlagInfo    : color = [UIColor greenColor];     break;
        case DDLogFlagDebug   : color = [UIColor whiteColor];     break;
        default               : color = [UIColor lightGrayColor]; break;
    }
    return color;
}

- (NSIndexPath *)tableView:(UITableView *)tableView willSelectRowAtIndexPath:(NSIndexPath *)indexPath{
    DDLogMessage * logMessage = (_logger.filteringEnabled ? _logger.filteredMessages : _logger.messages)[indexPath.row];
    if ([_logger.expandedMessages containsObject:logMessage]){
        [_logger.expandedMessages removeObject:logMessage];
    }
    else{
        [_logger.expandedMessages addObject:logMessage];
    }
    
    UILabel * label = (UILabel *)[tableView cellForRowAtIndexPath:indexPath].contentView.subviews[0];
    label.text = [self textForCellWithLogMessage:logMessage];
    
    dispatch_async(_logger.consoleQueue, ^
                   {
                       [_logger updateTableViewInConsoleQueue];
                   });
    
    return nil;
}
#pragma mark - Copying text

- (BOOL)tableView:(UITableView *)tableView shouldShowMenuForRowAtIndexPath:(NSIndexPath *)indexPath{
    return YES;
}

- (BOOL)tableView:(UITableView *)tableView canPerformAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender{
    return action == @selector(copy:);
}

- (void)tableView:(UITableView *)tableView performAction:(SEL)action forRowAtIndexPath:(NSIndexPath *)indexPath withSender:(id)sender{
    if (action == @selector(copy:)){
        DDLogMessage * logMessage = (_logger.filteringEnabled ? _logger.filteredMessages : _logger.messages)[indexPath.row];
        NSString * textToCopy = [_logger formatLogMessage:logMessage];
        UIPasteboard.generalPasteboard.string = textToCopy;
    }
}


#pragma mark - cell text
- (NSString *)textForCellWithLogMessage:(DDLogMessage *)logMessage{
    NSString *prefix;
    switch (logMessage->_flag){
        case DDLogFlagError   : prefix = @"Ⓔ"; break;
        case DDLogFlagWarning : prefix = @"Ⓦ"; break;
        case DDLogFlagInfo    : prefix = @"Ⓘ"; break;
        case DDLogFlagDebug   : prefix = @"Ⓓ"; break;
        default               : prefix = @"Ⓥ"; break;
    }

    // 展开
    if ([_logger.expandedMessages containsObject:logMessage]){
        return [NSString stringWithFormat:@" %@ %@", prefix, [_logger formatLogMessage:logMessage]];
    }

    // 搜索
    return [NSString stringWithFormat:@" %@ %@", prefix, [_logger formatShortLogMessage:logMessage]];
}

- (UILabel *)labelForNewCell{
    UILabel * label = [UILabel new];
    label.backgroundColor = [UIColor clearColor];
    label.font = _logger.font;
    label.autoresizingMask = UIViewAutoresizingFlexibleHeight | UIViewAutoresizingFlexibleWidth;
    label.numberOfLines = 0;

    return label;
}


@end
