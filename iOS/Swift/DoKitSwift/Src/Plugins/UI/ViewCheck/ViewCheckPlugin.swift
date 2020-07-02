//
//  ViewCheckPlugin.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

struct ViewCheckPlugin: Plugin {
    
    var module: PluginModule { .ui }
    
    var title: String { LocalizedString("组件检查") }
    
    var icon: UIImage? { DKImage(named: "doraemon_view_check") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        ViewCheck.shared.show()
        VisualInfo.defalut.show()
        VisualInfo.defalut.frame = VisualInfo.rect
        HomeWindow.shared.hide()
    }
}

extension VisualInfo {
    
    fileprivate static var rect: CGRect {
        let height: CGFloat = kSizeFrom750_Landscape(180)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
        switch kOrientationPortrait {
        case true:
            return .init(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * margin,
                height: height
            )
            
        case false:
            return .init(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * margin,
                height: height
            )
        }
    }
}
