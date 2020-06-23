//
//  CrashPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import Foundation

struct CrashPlugin: Plugin {

    var module: PluginModule { .performance }
    
    var title: String { LocalizedString("Crash") }
    
    var icon: UIImage? { DKImage(named: "doraemon_crash") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        HomeWindow.shared.openPlugin(vc: CrashViewController())
    }
}
