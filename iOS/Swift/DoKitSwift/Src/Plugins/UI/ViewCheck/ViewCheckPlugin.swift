//
//  ViewCheckPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewCheckPlugin: Plugin {
    
    var module: String {
        return LocalizedString("视觉工具")
    }
    
    var title: String {
        return LocalizedString("组件检查")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_view_check")
    }
    
    func didLoad() {
        ViewCheck.shared.show()
        DoKitHomeWindow.shared.hide()
    }
}
