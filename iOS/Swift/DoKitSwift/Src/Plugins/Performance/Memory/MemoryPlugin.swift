//
//  MemoryPlugin.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/24.
//

import Foundation

struct MemoryPlugin: Plugin {
    static var isOn = false
    var module: PluginModule { .performance }
    var title: String { "Memory" }
    var icon: UIImage? { DKImage(named: "doraemon_kadun") }
    
    func onInstall() { /* do nothing */ }
    
    func onSelected() {
        HomeWindow.shared.openPlugin(vc: MemoryViewController())
    }
}
