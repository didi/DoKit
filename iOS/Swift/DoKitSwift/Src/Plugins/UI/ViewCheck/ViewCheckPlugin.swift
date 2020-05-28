//
//  ViewCheckPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewCheckPlugin: Plugin {
    
    var module: String {
        return LocalizedString("常用工具")
    }
    
    var title: String {
        return LocalizedString("清理缓存")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_qingchu")
    }
    
    func didLoad() {
        let vc = DelSanboxViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
