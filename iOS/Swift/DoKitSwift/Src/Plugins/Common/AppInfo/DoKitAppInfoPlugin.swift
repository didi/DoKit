//
//  DoKitAppInfoPlugin.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

class DoKitAppInfoPlugin: DoKitBasePlugin {
    override func pluginDidLoad() {
        let vc = DoKitAppInfoViewController()
        DoKitHomeWindow.shared.openPlugin(vc: vc)
    }
}
