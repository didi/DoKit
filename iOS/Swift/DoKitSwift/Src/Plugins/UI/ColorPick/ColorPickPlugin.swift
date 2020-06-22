//
//  ColorPickPlugin.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import Foundation

struct ColorPickPlugin: Plugin {

    var module: PluginModule { .UI }
    
    var title: String { LocalizedString("取色器") }
    
    var icon: UIImage? { DKImage(named: "doraemon_straw") }
    
    func onInstall() {
        
    }
    func onSelected() {
        ColorPickWindow.shared.show()
        ColorPickInfoWindow.shared.show()
        HomeWindow.shared.hide()
    }
}
