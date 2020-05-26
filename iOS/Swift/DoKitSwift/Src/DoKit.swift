//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

public class DoKit {
    var entryWindow:DoKitEntryWindow
    var originalPluginArray: Array<DoKitPluginModel>
    public var pluginArray: Array<Dictionary<String, Any>>
    public static let shared = DoKit()
    private init() {
        originalPluginArray = [DoKitPluginModel]()
        pluginArray = [Dictionary<String, Any>]()
        
        let startPoint = CGPoint(x: 0, y: kScreenHeight/3)
        entryWindow = DoKitEntryWindow(frame: CGRect(x: startPoint.x, y: startPoint.y, width: 58, height: 58))
        entryWindow.show()
    }
    
    public func install() {
        self.addPlugin(title: DoKitLocalizedString("Mock数据"), icon: "doraemon_mock", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("平台工具"))
        self.addPlugin(title: DoKitLocalizedString("Mock数据"), icon: "doraemon_mock", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("平台工具"))
        self.addPlugin(title: DoKitLocalizedString("应用设置"), icon: "doraemon_setting", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("App信息"), icon: "doraemon_app_info", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("应用设置"), icon: "doraemon_setting", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("App信息"), icon: "doraemon_app_info", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("应用设置"), icon: "doraemon_setting", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("App信息"), icon: "doraemon_app_info", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("常用工具"))
        self.addPlugin(title: DoKitLocalizedString("帧率"), icon: "doraemon_fps", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("性能检测"))
        self.addPlugin(title: DoKitLocalizedString("帧率"), icon: "doraemon_fps", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("性能检测"))
        self.addPlugin(title: DoKitLocalizedString("帧率"), icon: "doraemon_fps", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("性能检测"))
        self.addPlugin(title: DoKitLocalizedString("取色器"), icon: "doraemon_straw", plugin: "DoKitAppSettingPlugin", module: DoKitLocalizedString("视觉工具"))
        
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

}

public class DoKitPluginModel {
    var title: String!
    var icon: String!
    var plugin: String!
    var module: String!
}

