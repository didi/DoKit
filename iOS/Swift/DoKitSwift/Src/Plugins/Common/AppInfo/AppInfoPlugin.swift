//
//  AppInfoPlugin.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

class AppInfoPlugin: Plugin {
    var module: String {
        return LocalizedString("常用工具")
    }
    var title: String {
        return LocalizedString("App信息")
    }
    var icon: UIImage? {
        return UIImage.dokitImageNamed(name: "doraemon_app_info")
    }
    func onInstall() {
        
    }
    func onSelected() {        
        let vc = AppInfoViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
