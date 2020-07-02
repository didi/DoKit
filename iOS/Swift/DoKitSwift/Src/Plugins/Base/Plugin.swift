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

public struct PluginModule: Hashable {
    public let name: String
    
    public init(name: String) {
        self.name = name
    }
    
    public func hash(into hasher: inout Hasher) {
        hasher.combine(name)
    }
}

extension PluginModule {
    static let platform = PluginModule(name: LocalizedString("平台工具"))
    static let performance = PluginModule(name: LocalizedString("性能检测"))
    static let ui = PluginModule(name: LocalizedString("视觉工具"))
    static let common = PluginModule(name: LocalizedString("常用工具"))
}


