//
//  ColorPickPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import Foundation

struct ColorPickPlugin: Plugin {

    var module: String { LocalizedString("视觉工具") }
    
    var title: String { LocalizedString("取色器") }
    
    var icon: UIImage? { UIImage("doraemon_straw") }
    
    func onInstall() {
        
    }
    func onSelected() {
        ColorPickWindow.shared.show()
        ColorPickInfoWindow.shared.show()
        HomeWindow.shared.hide()
    }
}
