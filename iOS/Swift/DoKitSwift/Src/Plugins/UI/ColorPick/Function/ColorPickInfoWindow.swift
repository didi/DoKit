//
//  ColorPickInfoWindow.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import UIKit

class ColorPickInfoWindow: UIWindow {
    
    private struct Const {
        let height: CGFloat = kSizeFrom750_Landscape(100)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
    }

    static let shared = ColorPickInfoWindow(frame: ColorPickInfoWindow.getRect())
    
    static private func getRect() -> CGRect {
        let const = Const()
        switch kOrientationPortrait {
        case true:
            return CGRect(
                x: const.margin,
                y: kScreenHeight - const.height - const.margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * const.margin,
                height: const.height
            )
            
        case false:
            return CGRect(
                x: const.margin,
                y: kScreenHeight - const.height - const.margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * const.margin,
                height: const.height
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
