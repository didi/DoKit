//
//  ColorPickPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import Foundation

struct ColorPickPlugin: Plugin {

    var module: String { DoKitLocalizedString("视觉工具") }
    
    var title: String { DoKitLocalizedString("取色器") }
    
    var icon: UIImage? { UIImage.dokitImageNamed(name: "doraemon_setting") }
    
    func didLoad() {
        ColorPickWindow.shared.show()
        ColorPickInfoWindow.shared.show()
        DoKitHomeWindow.shared.hide()
    }
}
