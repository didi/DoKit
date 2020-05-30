//
//  DoKitAppSettingPlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import Foundation


struct AppSettingPlugin: Plugin {
    
    var module: String {
        return LocalizedString("常用工具")
    }
    
    var title: String {
        return LocalizedString("应用设置")
    }
    
    var icon: UIImage? {
        return DKImage(named: "doraemon_setting")
    }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        DoKitUtil.openAppSetting()
        HomeWindow.shared.hide()
    }
}
