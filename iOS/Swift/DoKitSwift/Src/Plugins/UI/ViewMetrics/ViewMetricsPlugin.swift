//
//  ViewMetricsPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewMetricsPlugin: Plugin {
    
    var module: PluginModule {.UI }
    
    var title: String { LocalizedString("布局边框") }
    
    var icon: UIImage? { DKImage(named: "doraemon_viewmetrics") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        ViewMetrics.shared.enable.toggle()
        HomeWindow.shared.hide()
        // toast
        print(ViewMetrics.shared.enable ? "布局边框已开启" : "布局边框已关闭")
    }
}
