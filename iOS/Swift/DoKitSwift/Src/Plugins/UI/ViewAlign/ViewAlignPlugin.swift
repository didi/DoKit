//
//  ViewAlignPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

class ViewAlignPlugin: Plugin{
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
