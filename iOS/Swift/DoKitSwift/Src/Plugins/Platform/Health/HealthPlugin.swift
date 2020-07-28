//
//  HealthPlugin.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/23.
//

import UIKit

struct HealthPlugin: Plugin {
    var module: PluginModule { .platform }

    var title: String { LocalizedString("健康体检") }

    var icon: UIImage? { DKImage(named: "doraemon_health") }

    func onInstall() {
        
    }
    
    func onSelected() {
        let vc = HealthViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
