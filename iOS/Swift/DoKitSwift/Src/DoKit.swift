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
    var pluginMap = [PluginModule: [Plugin]]()
    var modules = [PluginModule]()
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
        //日志收集
        addPlugin(plugin: LogPlugin())
        
        addPlugin(plugin: ANRPlugin())
        addPlugin(plugin: UIProfilePlugin())
        setup()
    }
    

    public var H5DoorBlock: ((_ h5Url: String) -> Void)?
    public func addH5DoorBlock(blcok: @escaping (_ h5Url: String) -> Void) {
        H5DoorBlock = blcok
    }
}

// MARK:- Public
public extension DoKit {
    
    func showDoKit() {
        if entryWindow.isHidden {
            entryWindow.isHidden = false
        }
    }
    
    func hideDoKit() {
        if !entryWindow.isHidden {
            entryWindow.isHidden = true
        }
    }
    
    func addPlugin(plugin:Plugin){
        plugin.onInstall()
        if pluginMap[plugin.module] != nil {
            pluginMap[plugin.module]?.append(plugin)
        }else{
            modules.append(plugin.module)
            pluginMap[plugin.module] = [plugin]
        }
    }
    
    func addPlugin(module: PluginModule,title: String, icon: UIImage?,onInstall: (()->Void)?,onSelected: (()->Void)?){
        let plugin = DefaultPlugin.init(module: module, title: title, icon: icon, onInstallClosure: onInstall,onSelectedClosure: onSelected)
        addPlugin(plugin: plugin)
    }
}

private extension DoKit {
    
    func setup() {
        //根据开关判断是否收集Crash日志
        if CacheManager.shared.isOnCrash {
            Crash.register()
        }
    }
}
