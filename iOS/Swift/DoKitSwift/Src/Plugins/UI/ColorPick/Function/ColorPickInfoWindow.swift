//
//  ColorPickInfoWindow.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import UIKit

class ColorPickInfoWindow: UIWindow {

    static let shared = ColorPickInfoWindow(frame: ColorPickInfoWindow.rect)
    
    static private var rect: CGRect {
        let height: CGFloat = kSizeFrom750_Landscape(100)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
        switch kOrientationPortrait {
        case true:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * margin,
                height: height
            )
            
        case false:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * margin,
                height: height
            )
        }
    }
}

extension ColorPickInfoWindow {
    
    func show() {
        isHidden = false
    }
    
    func hide() {
        isHidden = true
    }
    
    func set(current Color: UIColor) {
        
    }
}
