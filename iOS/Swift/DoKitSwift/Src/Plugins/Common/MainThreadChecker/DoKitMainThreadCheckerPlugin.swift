//
//  DoKitMainThreadCheckerPlugin.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

class DoKitMainThreadCheckerPlugin: Plugin{
    var module: String {
        return DoKitLocalizedString("常用工具")
    }
    
    var title: String {
        return DoKitLocalizedString("子线程UI")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_ui")
    }
    
    func didLoad() {
        let vc = DoKitMainThreadCheckerViewController()
        DoKitHomeWindow.shared.openPlugin(vc: vc)
    }
}
