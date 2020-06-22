//
//  LaunchTimePlugin.swift
//  DoraemonKit-Swift
//
//  Created by objc on 2020/5/29.
//

import Foundation
import UIKit

struct LaunchTimePlugin: Plugin {

    var module: PluginModule { .performance }

    var title: String { LocalizedString("启动耗时") }

    var icon: UIImage? { DKImage(named: "doraemon_app_start_time") }

    func onSelected() {
        let vc = LaunchTimeViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
