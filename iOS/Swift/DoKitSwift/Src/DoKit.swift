//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

public class DoKit {
    public static let shared = DoKit()
    public var isShowDoKit: Bool {
        get {
            return !entryWindow.isHidden
        }
    }
    var pluginMap = [String: [Plugin]]()
    var modules = [String]()
    var entryWindow:DoKitEntryWindow
    private init() {
        let startPoint = CGPoint(x: 0, y: kScreenHeight/3)
        entryWindow = DoKitEntryWindow(frame: CGRect(x: startPoint.x, y: startPoint.y, width: 58, height: 58))
        entryWindow.show()
    }
    
    public func install() {
        self.addPlugin(plugin: DoKitAppSettingPlugin())
        self.addPlugin(plugin: DoKitDelSanboxPlugin())
        self.addPlugin(plugin: DoKitMainThreadCheckerPlugin())
        addPlugin(plugin: ViewAlignPlugin())
        addPlugin(plugin: ViewCheckPlugin())
    }
    
    public func addPlugin(plugin:Plugin){
        if pluginMap[plugin.module] != nil {
            pluginMap[plugin.module]?.append(plugin)
        }else{
            self.modules.append(plugin.module)
            pluginMap[plugin.module] = [plugin]
        }
    }
    
    public func addPlugin(module: String,title: String, icon: UIImage?,didLoad:@escaping ()->Void){
        let plugin = DefaultPlugin.init(module: module, title: title, icon: icon, callBack: didLoad)
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

}
