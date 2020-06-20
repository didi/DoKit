//
//  App.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/6/20.
//

import Foundation

enum App {
    
    /// 应用版本号
    static var version: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String ?? ""
    }
    
    /// 应用名称
    static var name: String {
        Bundle.main.object(forInfoDictionaryKey: "CFBundleDisplayName") as? String ?? ""
    }
    
    /// 系统版本
    static var osVersion: String { ProcessInfo.processInfo.operatingSystemVersionString }
    
    static var iphoneName: String { UIDevice.current.name }
    
    static var systemVersion: String { UIDevice.current.systemVersion }
    
    static var bundleIdentifier: String? { Bundle.main.bundleIdentifier }
}
