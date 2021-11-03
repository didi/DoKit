//
//  DoraemonDDLogMessage.h
//  DoraemonKit
//
//  Created by yixiang on 2018/12/6.
//

#import <Foundation/Foundation.h>
#import <CocoaLumberjack/CocoaLumberjack.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonDDLogMessage : NSObject

@property (nonatomic, copy) NSDate *timestamp;
@property (nonatomic, assign) DDLogFlag flag;
@property (nonatomic, copy) NSString *message;
@property (nonatomic, copy) NSString *fileName;
@property (nonatomic, assign) NSInteger line;
@property (nonatomic, copy) NSString *threadId;
@property (nonatomic, copy) NSString *threadName;
@property (nonatomic, assign) BOOL expand;


@end

NS_ASSUME_NONNULL_END
