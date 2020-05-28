//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import DoraemonKit_Swift

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {
    var window: UIWindow?

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        DoKit.shared.addPlugin(title: "环境切换", icon: "doraemon_default", plugin: "TestPlugin",module: "业务工具")
        DoKit.shared.install()

        self.window?.frame = UIScreen.main.bounds;
        let homeVc = DoraemonDemoHomeViewController()
        let nav = UINavigationController(rootViewController: homeVc)
        self.window?.rootViewController = nav
        self.window?.makeKeyAndVisible()
        return true
    }


}

