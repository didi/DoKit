//
//  UserDefaultsPlugin.swift
//  DoraemonKit-Swift
//
//  Created by Smallfly on 2020/7/27.
//

import Foundation

struct UserDefaultsPlugin: Plugin {
    var module: PluginModule {
        return .common
    }
    
    var title: String {
        return "UserDefaults"
    }
    
    var icon: UIImage? {
        return DKImage(named: "doraemon_database")
    }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        HomeWindow.shared.openPlugin(vc: UserDefaultsViewController())
    }

}
