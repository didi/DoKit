//
//  LaunchTime.swift
//  DoraemonKit-Swift
//
//  Created by objc on 2020/6/2.
//

import UIKit

extension UIWindow {
    override open func motionBegan(_ motion: UIEvent.EventSubtype, with event: UIEvent?) {
        super.motionBegan(motion, with: event)

        guard isKeyWindow, motion == .motionShake else {
            return
        }

        if HomeWindow.shared.isHidden {
            HomeWindow.shared.show()
        } else {
            HomeWindow.shared.hide()
        }
    }
}
