//
//  DoKitMainThreadCheckerPlugin.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

struct MainThreadCheckerPlugin: Plugin {
    
    var module: PluginModule { .performance }
    
    var title: String { LocalizedString("子线程UI") }
    
    var icon: UIImage? {
        return DKImage(named: "doraemon_ui")
    }
    
    func onInstall() {
        MainThreadCheckerManager.swizzleIfNeeded
    }
    
    func onSelected() {
        HomeWindow.shared.openPlugin(vc: MainThreadCheckerViewController())
    }
    
    
}



