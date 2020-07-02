//
//  LogPlugin.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/10.
//

import Foundation
class LogPlugin: Plugin {
    var module: PluginModule {
        return .common
    }
    var title: String {
        return LocalizedString("Logs")
    }
    var icon: UIImage? {
        return DKImage(named: "doraemon_log")
    }
    func onInstall() {
        let isOn = LogManager.shared.isOn;
        if isOn {
            LogManager.shared.start()
        }
    }
    func onSelected() {
        let vc = LogViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
    
}
