//
//  ViewAlignPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

struct ViewAlignPlugin: Plugin {
    
    var module: String {
        return DoKitLocalizedString("视觉工具")
    }
    
    var title: String {
        return DoKitLocalizedString("对齐标尺")
    }
    
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_align")
    }
    
    func didLoad() {
        ViewAlign.shared.show()
        DoKitHomeWindow.shared.hide()
    }
}
