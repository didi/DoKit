//
//  CrashPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import Foundation

struct CrashPlugin: Plugin {

    var module: String { LocalizedString("性能检测") }
    
    var title: String { LocalizedString("Crash") }
    
    var icon: UIImage? { DKImage(named: "doraemon_crash") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        // TODO: - Test
        Crash.handler = { model in
            let info = """
            ======================== \(model.type.rawValue)异常错误报告 ========================
            name: \(model.name)
            reason: \(model.reason ?? "unknown")
            
            Call Stack:
            \(model.symbols.joined(separator: "\r"))
            
            ThreadInfo:
            \(Thread.current.description)
            
            AppInfo:
            \(CrashPlugin.appInfo)
            """
            /// 存沙盒或者发邮件
            try? Crash.Tool.save(crash: info, file:  "\(model.type.rawValue)")
        }
        Crash.registerHandler()
        HomeWindow.shared.openPlugin(vc: CrashViewController())
    }
}

extension CrashPlugin {
    
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
