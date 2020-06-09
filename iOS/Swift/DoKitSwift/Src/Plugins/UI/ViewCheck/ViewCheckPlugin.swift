//
//  ViewCheckPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewCheckPlugin: Plugin {
    
    var module: String { LocalizedString("视觉工具") }
    
    var title: String { LocalizedString("组件检查") }
    
    var icon: UIImage? { DKImage(named: "doraemon_view_check") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        ViewCheck.shared.show()
        HomeWindow.shared.hide()
    }
}
