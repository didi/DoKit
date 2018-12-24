//
//  DoraemonCrashTool.h
//  DoraemonKit
//
//  Created by wenquan on 2018/11/22.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonCrashTool : NSObject

/**
 保存崩溃日志到沙盒中的Library/Caches/Crash目录下
 
 @param log 崩溃日志的内容
 @param fileName 保存的文件名
 */
+ (void)saveCrashLog:(NSString *)log fileName:(NSString *)fileName;

/**
 获取崩溃日志的目录

 @return 崩溃日志的目录
 */
+ (NSString *)crashDirectory;

@end

NS_ASSUME_NONNULL_END
