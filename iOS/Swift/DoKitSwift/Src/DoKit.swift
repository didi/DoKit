//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

public class DoKit {
    public var pluginArray: Array<Dictionary<String, Any>>
    public static let shared = DoKit()
    public var isShowDoKit: Bool {
        get {
            return !entryWindow.isHidden
        }
    }
    
    var entryWindow:DoKitEntryWindow
       var originalPluginArray: Array<DoKitPluginModel>
    private init() {
        originalPluginArray = [DoKitPluginModel]()
        pluginArray = [Dictionary<String, Any>]()
        
        let startPoint = CGPoint(x: 0, y: kScreenHeight/3)
        entryWindow = DoKitEntryWindow(frame: CGRect(x: startPoint.x, y: startPoint.y, width: 58, height: 58))
        entryWindow.show()
    }
    
    public func install() {
        self.addPlugin(title: DoKitLocalizedString("应用设置"), icon: "doraemon_setting", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("清理缓存"), icon: "doraemon_qingchu", plugin: "DoKitDelSanboxPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("子线程UI"), icon: "doraemon_ui", plugin: "DoKitMainThreadCheckerPlugin", module: DoKitLocalizedString("常用工具"))
        
        var modules = Array<String>()
        for plugin in originalPluginArray {
            let module = plugin.module
            if !modules.contains(module!) {
                modules.append(module!)
            }
        }
        
        for module in modules {
            var moduleDic = [String: Any]()
            moduleDic["module"] = module
            var plugins = [DoKitPluginModel]()
            for plugin in originalPluginArray {
                if module == plugin.module {
                    plugins.append(plugin)
                }
            }
            moduleDic["pluginArray"] = plugins
            pluginArray.append(moduleDic)
        }
    }
    
    public func addPlugin(title: String, icon: String, plugin: String, module: String) {
        let pluginModel = DoKitPluginModel()
        pluginModel.title = title
        pluginModel.icon = icon
        pluginModel.plugin = plugin
        pluginModel.module = module
        originalPluginArray.append(pluginModel)
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

}

public class DoKitPluginModel {
    var title: String!
    var icon: String!
    var plugin: String!
    var module: String!
}

