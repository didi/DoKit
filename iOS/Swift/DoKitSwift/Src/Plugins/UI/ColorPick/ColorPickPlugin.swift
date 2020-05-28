//
//  ColorPickPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import Foundation

struct ColorPickPlugin: Plugin {

    var module: String {
        return DoKitLocalizedString("视觉工具")
    }
    
    var title: String {
        return DoKitLocalizedString("取色器")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_setting")
    }
    
    func didLoad() {
        ColorPickWindow.shared.show()
        ColorPickInfoWindow.shared.show()
        DoKitHomeWindow.shared.hide()
    }
}
