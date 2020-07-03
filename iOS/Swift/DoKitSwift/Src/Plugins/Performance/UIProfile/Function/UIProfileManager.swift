//
//  UIProfileManager.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

import Foundation

struct UIProfileManager {
    
    static var shared = UIProfileManager()
    
    var isEnable = false
    
    func start() {
        UIViewController.topViewControllerForKeyWindow()?.profileViewDepth()
    }
    
    func stop() {
        UIViewController.topViewControllerForKeyWindow()?.resetProfileData()
        UIProfileWindow.shared.hide()
    }
}
