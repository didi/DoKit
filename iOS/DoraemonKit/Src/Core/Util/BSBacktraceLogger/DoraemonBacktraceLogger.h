//
//  DoraemonBacktraceLogger.h
//  DoraemonKit
//
//  Created by didi on 2020/3/18.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

#define DoraemonBSLOG NSLog(@"%@",[BSBacktraceLogger bs_backtraceOfCurrentThread]);
#define DoraemonBSLOG_MAIN NSLog(@"%@",[BSBacktraceLogger bs_backtraceOfMainThread]);
#define DoraemonBSLOG_ALL NSLog(@"%@",[BSBacktraceLogger bs_backtraceOfAllThread]);

@interface DoraemonBacktraceLogger : NSObject

+ (NSString *)doraemon_backtraceOfAllThread;
+ (NSString *)doraemon_backtraceOfCurrentThread;
+ (NSString *)doraemon_backtraceOfMainThread;
+ (NSString *)doraemon_backtraceOfNSThread:(NSThread *)thread;

@end

NS_ASSUME_NONNULL_END
