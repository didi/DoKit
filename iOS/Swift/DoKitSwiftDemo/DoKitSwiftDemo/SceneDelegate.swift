//
//  SceneDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import DoraemonKit

class SceneDelegate: UIResponder, UIWindowSceneDelegate {

    var window: UIWindow?


    func scene(_ scene: UIScene, willConnectTo session: UISceneSession, options connectionOptions: UIScene.ConnectionOptions) {
        // Use this method to optionally configure and attach the UIWindow `window` to the provided UIWindowScene `scene`.
        // If using a storyboard, the `window` property will automatically be initialized and attached to the scene.
        // This delegate does not imply the connecting scene or session are new (see `application:configurationForConnectingSceneSession` instead).
        guard let _ = (scene as? UIWindowScene) else { return }
        
        DoraemonManager.shareInstance().addPlugin(withTitle: DoraemonDemoLocalizedString("测试插件"), icon: "doraemon_default", desc: DoraemonDemoLocalizedString("测试插件"), pluginName: "TestPlugin", atModule: DoraemonDemoLocalizedString("业务工具"))
        
        DoraemonManager.shareInstance().addPlugin(withTitle: DoraemonDemoLocalizedString("block方式加入插件"), icon: "doraemon_default", desc: DoraemonDemoLocalizedString("测试插件"), pluginName: "", atModule: DoraemonDemoLocalizedString("业务工具")) { (itemData: [AnyHashable : Any]) in
            print(itemData)
        }
        DoraemonManager.shareInstance().install()

        self.window = UIWindow(windowScene: scene as! UIWindowScene)
        self.window?.frame = UIScreen.main.bounds;
        let homeVc = DoraemonDemoHomeViewController()
        let nav = UINavigationController(rootViewController: homeVc)
        self.window?.rootViewController = nav
        self.window?.makeKeyAndVisible()
        
    }

    func sceneDidDisconnect(_ scene: UIScene) {
        // Called as the scene is being released by the system.
        // This occurs shortly after the scene enters the background, or when its session is discarded.
        // Release any resources associated with this scene that can be re-created the next time the scene connects.
        // The scene may re-connect later, as its session was not neccessarily discarded (see `application:didDiscardSceneSessions` instead).
    }

    func sceneDidBecomeActive(_ scene: UIScene) {
        // Called when the scene has moved from an inactive state to an active state.
        // Use this method to restart any tasks that were paused (or not yet started) when the scene was inactive.
    }

    func sceneWillResignActive(_ scene: UIScene) {
        // Called when the scene will move from an active state to an inactive state.
        // This may occur due to temporary interruptions (ex. an incoming phone call).
    }

    func sceneWillEnterForeground(_ scene: UIScene) {
        // Called as the scene transitions from the background to the foreground.
        // Use this method to undo the changes made on entering the background.
    }

    func sceneDidEnterBackground(_ scene: UIScene) {
        // Called as the scene transitions from the foreground to the background.
        // Use this method to save data, release shared resources, and store enough scene-specific state information
        // to restore the scene back to its current state.
    }


}

