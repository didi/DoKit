//
//  HealthManager.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthManager: NSObject {
    public static let shared = HealthManager()
    
    /// 是否启动
    public var start: Bool = false
    //本次启动时间 单位ms
    public var startTime: CGFloat?
    //启动流程耗时详情
    public var costDetail: NSString?
    //用例名称
    public var caseName: NSString?
    //测试人名称
    public var testPerson: NSString?
    
    public func rebootAppForHealthCheck() {
        
    }
    
    public func startHealthCheck() {
        start = true
        if start {
//            ANRManager.shared.start()
//            UIProfileManager.shared.enable = true
        }
    }
    
    public func stopHealthCheck() {
        start = false
        
    }
    
    public func startEnterPage(vcClass: AnyClass) {
        
    }
    
    public func enterPage(vcClass: AnyClass) {
        
    }
    
    public func leavePage(vcClass: AnyClass) {
        
    }
    
//    public func addHttpModel(httpModel: NetFlowHttpModel) {
//
//    }
    
    public func addANRInfo(anrInfo: NSDictionary) {
        
    }
    
    public func addSubThreadUI(info: NSDictionary) {
        
    }
    
    public func addUILevel(info: NSDictionary) {
        
    }
    
    public func addLeak(info: NSDictionary) {
        
    }
    
    public func openH5Page(h5Url: String) {
        
    }
    
    public func blackList(vcClass: AnyClass) -> Bool {
        return false
    }
}
