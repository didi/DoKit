//
//  ViewMetricsPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewMetricsPlugin: Plugin {
    
    var module: String { LocalizedString("视觉工具") }
    
    var title: String { LocalizedString("布局边框") }
    
    var icon: UIImage? { UIImage.dokitImageNamed(name: "doraemon_viewmetrics") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        
    }
}
