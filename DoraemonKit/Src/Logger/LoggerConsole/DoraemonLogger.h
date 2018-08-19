//
//  DoraemonLogger.h
//  CocoaLumberjack
//
//  Created by yixiang on 2017/12/27.
//

#import <CocoaLumberjack/CocoaLumberjack.h>
#import "DoraemonLoggerConsoleTableView.h"

@protocol DoraemonLoggerDelegate<NSObject>

-(void)addNewLog:(DDLogMessage *)logMessage;

-(void)loggerReloadData;

-(void)loggerUpdateTableViewRowsRemoving:(NSInteger)itemsToRemoveCount
                               inserting:(NSInteger)itemsToInsertCount;

@end

@interface DoraemonLogger : DDAbstractLogger

//显示最大条数，默认100条
@property (nonatomic, assign) NSUInteger maxMessages;

@property (nonatomic, weak) id<DDLogFormatter> shortLogFormatter;

@property (nonatomic, weak) id<DoraemonLoggerDelegate> delegate;

//消息
@property (nonatomic, strong) dispatch_queue_t consoleQueue;
@property (nonatomic, strong) NSMutableArray *messages;// 当前显示的log
@property (nonatomic, strong) NSMutableArray *addMessagesBuffer; //等待加入messages的log

//更新管理
@property (nonatomic, assign) BOOL updateScheduled;
@property (nonatomic, assign) NSTimeInterval minIntervalToUpdate;
@property (nonatomic, strong) NSDate *lastUpdate;

//刷选
@property (nonatomic, assign) BOOL filteringEnabled;//是否处于刷选状态
@property (nonatomic, strong) NSString *currentSearchText;//当前刷选文字内容
@property (nonatomic, assign) NSInteger currentLogLevel;//log等级
@property (nonatomic, strong) NSMutableArray *filteredMessages;//刷选之后的log

//扩展收起cell
@property (nonatomic, strong) NSMutableSet *expandedMessages;

// UI
@property (nonatomic, strong) UIFont *font;
@property (nonatomic, assign) CGFloat fontSize;

- (void)clearConsole;

- (void)updateTableViewInConsoleQueue;

- (NSString *)formatLogMessage:(DDLogMessage *)logMessage;

- (NSString *)formatShortLogMessage:(DDLogMessage *)logMessage;

@end
