//
//  DoKitAppSettingPlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import Foundation


struct DoKitAppSettingPlugin: Plugin {
    
    var module: String {
        return DoKitLocalizedString("常用工具")
    }
    
    var title: String {
        return DoKitLocalizedString("应用设置")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_setting")
    }
    
    func didLoad() {
        DoKitUtil.openAppSetting()
        DoKitHomeWindow.shared.hide()
    }
}
