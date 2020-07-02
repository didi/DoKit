//
//  H5Plugin.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

struct H5Plugin: Plugin {
    
    var module: PluginModule {
        return .common
    }
    
    var title: String {
        return LocalizedString("H5任意门")
    }
    
    var icon: UIImage? {
        return DKImage(named: "doraemon_h5")
    }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        let vc = H5ViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
