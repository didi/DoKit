//
//  LogPlugin.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/10.
//

import Foundation
class LogPlugin: Plugin {
    var module: String {
        return LocalizedString("常用工具")
    }
    var title: String {
        return LocalizedString("Logs")
    }
    var icon: UIImage? {
        return DKImage(named: "doraemon_log")
    }
    func onInstall() {
        
    }
    func onSelected() {
        let vc = LogViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
