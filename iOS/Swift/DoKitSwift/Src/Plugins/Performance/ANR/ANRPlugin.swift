//
//  ANRPlugin.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/20.
//

import UIKit

struct ANRPlugin: Plugin {
    var module: PluginModule { .performance }
    var title: String { LocalizedString("卡顿") }
    var icon: UIImage? { DKImage(named: "doraemon_kadun") }
    
    func onInstall() {
        ANRManager.sharedInstance.addANRBlock { (info) -> (Void) in
            print("anrDict = \(info)")
        }
    }
    
    func onSelected() {
        HomeWindow.shared.openPlugin(vc: ANRViewController())
    }
}
