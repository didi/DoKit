//
//  DoKitBasePlugin.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/26.
//

import UIKit



public protocol Plugin {
    var module: PluginModule { get }
    var title: String { get }
    var icon: UIImage? { get }
    
    /// 当组件被安装时调用
    func onInstall()
    /// 当组件在主页面被选中时调用
    func onSelected()
}

public enum PluginModule {
    case performance
    case UI
    case common
    case customize
    
    var name: String {
        switch self {
        case .performance:
            return LocalizedString("性能检测")
        case .UI:
            return LocalizedString("视觉工具")
        case .common:
            return LocalizedString("常用工具")
        case .customize:
            return LocalizedString("业务工具")
        }
    }
}

struct DefaultPlugin: Plugin {

    var module: PluginModule
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
