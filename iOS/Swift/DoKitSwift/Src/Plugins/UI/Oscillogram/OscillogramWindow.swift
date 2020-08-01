//
//  OscillogramWindow.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/24.
//

import UIKit

class OscillogramWindow: UIWindow {

    override init(frame: CGRect) {
        super.init(frame: frame)
        windowLevel = .statusBar + 2
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        return handle(point: point, event: event)
    }
    
    private func handle(point: CGPoint, event: UIEvent?) -> Bool {
        let b = subviews.reversed().map({recursion(subView: $0, point: point, event: event)})
        return b.first(where: {$0}) ?? false
    }
    
    private func recursion(subView: UIView, point: CGPoint, event: UIEvent?) -> Bool {
        let convertedPoint = subView.convert(point, from: self)
        if let candidate = subView.hitTest(convertedPoint, with: event), candidate is UIButton {
            return true
        }
        return false
    }
}
