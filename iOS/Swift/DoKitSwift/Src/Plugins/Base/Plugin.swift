//
//  DoKitBasePlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import UIKit

public protocol Plugin {
    var module: String { get }
    var title: String { get }
    var icon: UIImage? { get }
    
    /// 当组件被安装时调用
    func onInstall()
    /// 当组件在主页面被选中时调用
    func onSelected()
}

struct DefaultPlugin: Plugin {

    var module: String
    var title: String
    var icon: UIImage?
    var onInstallClosure: (()->Void)?
    var onSelectedClosure: (()->Void)?
    
    func onInstall() {
        self.onInstallClosure?()
    }
    func onSelected() {
        self.onSelectedClosure?()
    }
}
