//
//  ViewMetricsPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewMetricsPlugin: Plugin {
    
    var module: String {
        return DoKitLocalizedString("视觉工具")
    }
    
    var title: String {
        return DoKitLocalizedString("布局边框")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_viewmetrics")
    }
    
    func didLoad() {
        
    }
}
