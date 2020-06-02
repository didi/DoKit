//
//  ViewAlignPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

struct ViewAlignPlugin: Plugin {
    
    var module: String { LocalizedString("视觉工具") }
    
    var title: String { LocalizedString("对齐标尺") }
    
    var icon: UIImage? { DKImage(named: "doraemon_align") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        ViewAlign.shared.show()
        HomeWindow.shared.hide()
    }
}
