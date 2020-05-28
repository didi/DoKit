//
//  DoKitMainThreadCheckerPlugin.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

class DoKitMainThreadCheckerPlugin: DoKitBasePlugin {
    override func pluginDidLoad() {
        let vc = DoKitMainThreadCheckerViewController()
        DoKitHomeWindow.shared.openPlugin(vc: vc)
    }
}
