//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import DoraemonKit_Swift

extension PluginModule {
    static let customize = PluginModule(name: "业务工具")
}

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        DoKit.shared.addPlugin(module: .customize, title: "环境切换", icon: UIImage(named: "emoji"), onInstall: {
            print("启动了环境切换插件")
        }) {
            print("点击环境切换插件")
        }
        
//        DoKit.shared.addH5DoorBlock { h5Url in
//            print("使用自带容器打开H5链接: \(h5Url)")
//        }
        
        DoKit.shared.install()
        DoKit.shared.customAppInfo = {
            do {
                let path = Bundle.main.path(forResource: "build.json", ofType: nil)
                let newInfos:[[String]] = try JSONSerialization.jsonObject(with: Data.init(contentsOf: URL.init(fileURLWithPath: path!)) , options: .mutableLeaves) as! [[String]]
                return newInfos
            } catch {
                return []
            }
        }

        self.window?.frame = UIScreen.main.bounds;
        let homeVc = DoraemonDemoHomeViewController()
        let nav = UINavigationController(rootViewController: homeVc)
        self.window?.rootViewController = nav
        self.window?.makeKeyAndVisible()

        DoKit.shared.didFinishLaunching()
        return true
    }


}

