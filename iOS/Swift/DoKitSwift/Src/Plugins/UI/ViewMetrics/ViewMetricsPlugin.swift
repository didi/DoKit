//
//  ViewMetricsPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

class ViewMetricsPlugin: Plugin{
    var module: String {
        return DoKitLocalizedString("常用工具")
    }
    
    var title: String {
        return DoKitLocalizedString("清理缓存")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_qingchu")
    }
    
    func didLoad() {
        let vc = DoKitDelSanboxViewController()
        DoKitHomeWindow.shared.openPlugin(vc: vc)
    }
}
