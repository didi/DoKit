//
//  Crash.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/6/2.
//

import Foundation

enum Crash {
    public struct Model {
        
        public enum `Type`: String {
            case signal = "Signal"
            case exception = "Exception"
        }
        
        public let type: Type
        public let name: String
        public let reason: String?
        public let appinfo: String
        public let symbols: [String]
    }
    
    static var handler: ((Model) -> Void)?
    
    private static var app_old_exceptionHandler:(@convention(c) (NSException) -> Swift.Void)? = nil
    
    static func registerHandler() {
        setupSignalHandler()
        setupExceptionHandler()
    }
    
    private static func setupExceptionHandler() {
        app_old_exceptionHandler = NSGetUncaughtExceptionHandler()
        NSSetUncaughtExceptionHandler(Crash.recieveException)
    }
    
    private static func setupSignalHandler(){
        //http://stackoverflow.com/questions/36325140/how-to-catch-a-swift-crash-and-do-some-logging
        signal(SIGABRT, Crash.recieveSignal)
        signal(SIGBUS, Crash.recieveSignal)
        signal(SIGFPE, Crash.recieveSignal)
        signal(SIGILL, Crash.recieveSignal)
        signal(SIGPIPE, Crash.recieveSignal)
        signal(SIGSEGV, Crash.recieveSignal)
        signal(SIGSYS, Crash.recieveSignal)
        signal(SIGTRAP, Crash.recieveSignal)
    }
}

extension Crash {
    
    private static let recieveException: @convention(c) (NSException) -> Void = {
        (exteption) -> Void in
        app_old_exceptionHandler?(exteption)
        let model = Model(type: .exception,
                          name: "\(exteption.name)",
                          reason: exteption.reason,
                          appinfo: appInfo,
                          symbols: exteption.callStackSymbols)
        handler?(model)
        killApp()
    }
    
    private static let recieveSignal : @convention(c) (Int32) -> Void = {
        (signal) -> Void in
        var stack = Thread.callStackSymbols
        stack.removeFirst()
        let model = Model(type: .signal,
                          name: signal.signalName,
                          reason: "Signal \(signal.signalName)(\(signal)) was raised.",
                          appinfo: appInfo,
                          symbols: stack)
        
        handler?(model)
        killApp()
    }
    
    private static func killApp(){
        NSSetUncaughtExceptionHandler(nil)
        
        signal(SIGABRT, SIG_DFL)
        signal(SIGBUS, SIG_DFL)
        signal(SIGFPE, SIG_DFL)
        signal(SIGILL, SIG_DFL)
        signal(SIGPIPE, SIG_DFL)
        signal(SIGSEGV, SIG_DFL)
        signal(SIGSYS, SIG_DFL)
        signal(SIGTRAP, SIG_DFL)
        
        kill(getpid(), SIGKILL)
    }
    
    private static var appInfo: String {
        let displayName = Bundle.main.object(forInfoDictionaryKey: "CFBundleName") ?? ""
        let shortVersion = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") ?? ""
        let version = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") ?? ""
        let deviceModel = UIDevice.current.model
        let systemName = UIDevice.current.systemName
        let systemVersion = UIDevice.current.systemVersion
        return """
        App: \(displayName) \(shortVersion)(\(version))
        Device:\(deviceModel)
        OS Version:\(systemName) \(systemVersion)
        """
    }
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
}
