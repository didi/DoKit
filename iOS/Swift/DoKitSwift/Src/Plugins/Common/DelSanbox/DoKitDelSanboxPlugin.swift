//
//  DoKitDelSanboxPlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import UIKit

class DoKitDelSanboxPlugin: DoKitBasePlugin {
    override func pluginDidLoad() {
        let vc = DoKitDelSanboxViewController()
        DoKitHomeWindow.shared.openPlugin(vc: vc)
    }
}
