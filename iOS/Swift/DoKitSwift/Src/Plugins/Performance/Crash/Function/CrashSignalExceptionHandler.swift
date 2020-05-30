//
//  CrashSignalExceptionHandler.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//
// https://github.com/kstenerud/KSCrash

import Foundation


private typealias SignalHandler = (Int32, UnsafeMutablePointer<__siginfo>?, UnsafeMutableRawPointer?) -> Void
class CrashSignalExceptionHandler {
    
    static var shared = CrashSignalExceptionHandler()
    
    private struct SignalBox {
        let signal: Int32
        let handler: SignalHandler?
    }
    
    private let signals = [SIGABRT, SIGBUS, SIGFPE, SIGILL, SIGPIPE, SIGSEGV, SIGSYS, SIGTRAP]
    
    private var signalBoxs: [SignalBox] = []
    
    static func registerHandler() {
        shared.backupOriginalHandler()
        shared.signalRegister()
    }
}

extension CrashSignalExceptionHandler {
    
    private func backupOriginalHandler() {
        signalBoxs = signals.map { SignalBox(signal: $0, handler: $0.handler) }
    }
    
    private func signalRegister() {
        signals.forEach { register($0) }
        
    }
    
    private func register(_ signal: Int32) {
        var action = sigaction()
        action.__sigaction_u.__sa_sigaction = _signalHandler
        action.sa_flags = SA_NODEFER | SA_SIGINFO;
        sigemptyset(&action.sa_mask)
        sigaction(signal, &action, nil)
    }
    
    fileprivate func signalHandler(signal: Int32,
                                   info: UnsafeMutablePointer<__siginfo>?,
                                   context: UnsafeMutableRawPointer?) {
        var symbols = Thread.callStackSymbols
        var symbolInfo: String = ""
        // 这里过滤掉第一行日志
        // 因为注册了信号崩溃回调方法，系统会来调用，将记录在调用堆栈上，因此此行日志需要过滤掉
        if symbols.count > 0 {
            symbols.removeFirst()
            symbolInfo = symbols.joined(separator: "\n")
        }
        
        let content = """
        Signal Exception:
        Signal \(signal.signalName) was raised
        Call Stack:
        \(symbolInfo)
        threadInfo:
        \(Thread.current.description)
        """
        
        // 保存崩溃日志到沙盒cache目录
        CrashTool.save(crash: content, file: "Crash(Signal)")
        
        clearSignalRigister()
        
        // 调用之前崩溃的回调函数
        previous(signal: signal, info: info, context: context)
        
        kill(getpid(), SIGKILL);
    }
    
    private func clearSignalRigister() {
        signals.forEach { signal($0,SIG_DFL) }
    }
    
    private func previous(signal: Int32,
                          info: UnsafeMutablePointer<__siginfo>?,
                          context: UnsafeMutableRawPointer?) {
        
        let handler = signalBoxs.first { $0.signal == signal }?.handler
        handler?(signal, info, context)
    }
}

private func _signalHandler(signal: Int32,
                            info: UnsafeMutablePointer<__siginfo>?,
                            context: UnsafeMutableRawPointer?) {
    CrashSignalExceptionHandler.shared.signalHandler(signal: signal, info: info, context: context)
}

fileprivate extension Int32 {
    
    var signalName: String {
        switch self {
        case SIGABRT:   return "SIGABRT"
        case SIGBUS:    return "SIGBUS"
        case SIGFPE:    return "SIGFPE"
        case SIGILL:    return "SIGILL"
        case SIGPIPE:   return "SIGPIPE"
        case SIGSEGV:   return "SIGSEGV"
        case SIGSYS:    return "SIGSYS"
        case SIGTRAP:   return "SIGTRAP"
        default:        return "UNKNOWN"
        }
    }
    
    var handler: SignalHandler? {
        var action = sigaction()
        sigaction(self, nil, &action)
        guard let handler = action.__sigaction_u.__sa_sigaction else {
            return nil
        }
        return handler
    }
}
