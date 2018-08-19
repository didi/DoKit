 //
//  DoraemonLogger.m
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import "DoraemonLogger.h"

@interface DoraemonLogger()<DDLogFormatter,UISearchBarDelegate>

@end

@implementation DoraemonLogger

- (instancetype)init{
    self = [super init];
    if (self) {
        //default
        _maxMessages = 1000;
        _fontSize = 13.0;
        _font = [UIFont systemFontOfSize:_fontSize];
        _minIntervalToUpdate = 0.3;
        _lastUpdate = NSDate.date;
        _currentLogLevel = DDLogLevelVerbose;
        
        //queue
        _consoleQueue = dispatch_queue_create("console_queue", NULL);
        
        //init data
        _messages = [NSMutableArray arrayWithCapacity:_maxMessages];
        _addMessagesBuffer = NSMutableArray.array;
        _expandedMessages = NSMutableSet.set;
        
        
        [DDLog addLogger:self];
    }
    return self;
}

- (void)logMessage:(DDLogMessage *)logMessage{
    dispatch_async(_consoleQueue, ^{
        [_addMessagesBuffer insertObject:logMessage atIndex:0];
        [self updateOrScheduleTableViewUpdateInConsoleQueue];
        dispatch_sync(dispatch_get_main_queue(), ^{
            if (self.delegate && [self.delegate respondsToSelector:@selector(addNewLog:)]) {
                [self.delegate addNewLog:logMessage];
            }
        });
    });
}

- (void)clearConsole{
    dispatch_async(_consoleQueue, ^
                   {
                       // Clear all messages
                       [_addMessagesBuffer removeAllObjects];
                       [_messages removeAllObjects];
                       [_filteredMessages removeAllObjects];
                       [_expandedMessages removeAllObjects];
                       
                       [self updateTableViewInConsoleQueue];
                   });
}


- (void)updateOrScheduleTableViewUpdateInConsoleQueue{
    if (_updateScheduled) {
        return;
    }
    
    //每一次更新需要保证间隔在0.3s
    NSTimeInterval timeToWaitForNextUpdate = _minIntervalToUpdate + _lastUpdate.timeIntervalSinceNow;
    if (timeToWaitForNextUpdate > 0){
        _updateScheduled = YES;
        dispatch_after(dispatch_time(DISPATCH_TIME_NOW, (int64_t)(timeToWaitForNextUpdate * NSEC_PER_SEC)), _consoleQueue, ^
                       {
                           [self updateTableViewInConsoleQueue];
                           
                           _updateScheduled = NO;
                       });
    }
    else
    {
        [self updateTableViewInConsoleQueue];
    }
}

- (void)updateTableViewInConsoleQueue{

    [self preCheck];
    
    _lastUpdate = NSDate.date;
    
    __block NSInteger itemsToRemoveCount;
    __block NSInteger itemsToInsertCount;
    __block NSInteger itemsToKeepCount;
    void (^addAndTrimMessages)(NSMutableArray * messages, NSArray * newItems) = ^(NSMutableArray * messages, NSArray * newItems)
    {
        NSArray * tmp = [NSArray arrayWithArray:messages];
        [messages removeAllObjects];
        [messages addObjectsFromArray:newItems];
        [messages addObjectsFromArray:tmp];
        itemsToRemoveCount = MAX(0, (NSInteger)(messages.count - _maxMessages));
        if (itemsToRemoveCount > 0)
        {
            [messages removeObjectsInRange:NSMakeRange(_maxMessages, itemsToRemoveCount)];
        }
        itemsToInsertCount = MIN(newItems.count, _maxMessages);
        itemsToKeepCount = messages.count - itemsToInsertCount;
    };
    
    
    addAndTrimMessages(_messages, _addMessagesBuffer);
    
    // 处理刷选
    BOOL forceReload = NO;
    if (_filteringEnabled){
        if (!_filteredMessages){
            _filteredMessages = [self filterMessages:_messages];
            forceReload = YES;
        }
        
        addAndTrimMessages(_filteredMessages, [self filterMessages:_addMessagesBuffer]);
    }
    else{
        if (_filteredMessages){
            _filteredMessages = nil;
            forceReload = YES;
        }
    }
    
    [_addMessagesBuffer removeAllObjects];
    
    // Update table view (dispatch sync to ensure the messages' arrayt doesn't get modified)
    dispatch_async(dispatch_get_main_queue(), ^{
                      if (itemsToKeepCount == 0 || forceReload){
                          if (self.delegate && [self.delegate respondsToSelector:@selector(loggerReloadData)]) {
                              [self.delegate loggerReloadData];
                          }
                          
                      }else
                      {
                          if (self.delegate && [self.delegate respondsToSelector:@selector(loggerUpdateTableViewRowsRemoving:inserting:)]) {
                              [self.delegate loggerUpdateTableViewRowsRemoving:itemsToRemoveCount inserting:itemsToInsertCount];
                          }
                      }
                  });
}

//强制校验
- (void)preCheck{
    _filteringEnabled = (_currentSearchText.length > 0 ||
                         _currentLogLevel != DDLogLevelVerbose);
    if (_filteringEnabled){
        _filteredMessages = nil;
    }
}

- (NSMutableArray *)filterMessages:(NSArray *)messages{
    NSMutableArray * filteredMessages = NSMutableArray.array;
    for (DDLogMessage * message in messages)
    {
        if ([self messagePassesFilter:message])
        {
            [filteredMessages addObject:message];
        }
    }
    return filteredMessages;
}

- (BOOL)messagePassesFilter:(DDLogMessage *)message
{
    // Message is a marker OR (Log flag matches AND (no search text OR contains search text))
    return (((message->_flag & _currentLogLevel) &&
             (_currentSearchText.length == 0 ||
              [[self formatLogMessage:message] rangeOfString:_currentSearchText
                                                     options:(NSCaseInsensitiveSearch |
                                                              NSDiacriticInsensitiveSearch |
                                                              NSWidthInsensitiveSearch)].location != NSNotFound)));
}


#pragma mark - Log formatter

- (NSString *)formatLogMessage:(DDLogMessage *)logMessage{
    if (_logFormatter){
        return [_logFormatter formatLogMessage:logMessage];
    }
    else{
        return [NSString stringWithFormat:@"%@:%@ %@",
                logMessage.fileName,
                @(logMessage->_line),
                logMessage->_message];
    }
}

- (NSString *)formatShortLogMessage:(DDLogMessage *)logMessage{
    if (self.shortLogFormatter){
        return [self.shortLogFormatter formatLogMessage:logMessage];
    }
    else{
        return [[logMessage->_message
                 stringByReplacingOccurrencesOfString:@"  " withString:@""]
                stringByReplacingOccurrencesOfString:@"\n" withString:@" "];
    }
}

@end
