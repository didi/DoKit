//
//  DoKitMainThreadCheckerPlugin.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

struct MainThreadCheckerPlugin: Plugin {
    
    var module: String {
        return LocalizedString("常用工具")
    }
    
    var title: String {
        return LocalizedString("子线程UI")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_ui")
    }
    
    func didLoad() {
        let vc = MainThreadCheckerViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
