//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

public class DoKit {
    public static let shared = DoKit()
    public var isShowDoKit: Bool {
        get {
            return !entryWindow.isHidden
        }
    }
    var pluginMap = [String: [Plugin]]()
    var modules = [String]()
    var entryWindow:EntryWindow
    public var customAppInfo: (() -> [[String]])?
    private init() {
        let startPoint = CGPoint(x: 0, y: kScreenHeight/3)
        entryWindow = EntryWindow(frame: CGRect(x: startPoint.x, y: startPoint.y, width: 58, height: 58))
        entryWindow.show()
    }
    
    public func install() {
        addPlugin(plugin: CrashPlugin())
        addPlugin(plugin: AppSettingPlugin())
        addPlugin(plugin: AppInfoPlugin())
        addPlugin(plugin: DelSanboxPlugin())
        addPlugin(plugin: H5Plugin())
        addPlugin(plugin: MainThreadCheckerPlugin())
        addPlugin(plugin: ViewAlignPlugin())
        addPlugin(plugin: ViewCheckPlugin())
        addPlugin(plugin: ViewMetricsPlugin())
        addPlugin(plugin: ColorPickPlugin())

        // 性能检测
        addPlugin(plugin: LaunchTimePlugin())
    }
    
    public func addPlugin(plugin:Plugin){
        plugin.onInstall()
        if pluginMap[plugin.module] != nil {
            pluginMap[plugin.module]?.append(plugin)
        }else{
            self.modules.append(plugin.module)
            pluginMap[plugin.module] = [plugin]
        }
    }
    
    public func addPlugin(module: String,title: String, icon: UIImage?,onInstall: (()->Void)?,onSelected: (()->Void)?){
        let plugin = DefaultPlugin.init(module: module, title: title, icon: icon, onInstallClosure: onInstall,onSelectedClosure: onSelected)
        self.addPlugin(plugin: plugin)
    }
    
    public func showDoKit() {
        if entryWindow.isHidden {
            entryWindow.isHidden = false
        }
    }
    
    public func hideDoKit() {
        if !entryWindow.isHidden {
            entryWindow.isHidden = true
        }
    }

    public var H5DoorBlock: ((_ h5Url: String) -> Void)?
    public func addH5DoorBlock(blcok: @escaping (_ h5Url: String) -> Void) {
        H5DoorBlock = blcok
    }
}
