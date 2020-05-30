//
//  CrashUncaughtExceptionHandler.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import Foundation

enum CrashUncaughtExceptionHandler {
    static var previousUncaughtExceptionHandler = NSGetUncaughtExceptionHandler()
    
    static func registerHandler() {
        NSSetUncaughtExceptionHandler(uncaughtExceptionHandler)
    }
}

func uncaughtExceptionHandler(exception: NSException) {
    
    let info =
    """
        ========uncaughtException异常错误报告========
        name: \(exception.name)
        reason: \(exception.reason ?? "unknown")
        callStackSymbols:
        \(exception.callStackSymbols.joined(separator: "\n"))
    """
    // 保存崩溃日志到沙盒cache目录
    print(exception)
    CrashTool.save(crash: info, file: "Crash(Uncaught)")
    // 调用之前崩溃的回调函数
    CrashUncaughtExceptionHandler.previousUncaughtExceptionHandler?(exception)
    
    // 杀掉程序，这样可以防止同时抛出的SIGABRT被SignalException捕获
    kill(getpid(), SIGKILL);
 }
