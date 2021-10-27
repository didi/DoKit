//
//  AppDelegate.swift
//  Demo
//
//  Created by 唐佳诚 on 2021/9/22.
//

import UIKit
import DoraemonKit

@main
private class AppDelegate: UIResponder, UIApplicationDelegate {
    
    fileprivate var window: UIWindow?

    fileprivate func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        DoraemonManager.shareInstance().install()
        
        return true
    }

}

