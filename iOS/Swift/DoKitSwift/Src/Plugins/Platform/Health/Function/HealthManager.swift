//
//  HealthManager.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthManager: NSObject {
    public static let shared = HealthManager()
    
    /// 是否启动
    public var start: Bool = false
    //本次启动时间 单位ms
    public var startTime: CGFloat?
    // 启动流程耗时详情
    public var costDetail: NSString?
    // 用例名称
    public var caseName: NSString?
    // 测试人名称
    public var testPerson: NSString?
    
    private var secondTimer: Timer?
//    private var fpsUtil: DoraemonFPSUtil?
    private var firstEnter: Bool?
    private var h5UrlString: String?
    
    private var cpuPageArray = [String]()
    private var cpuArray = [[String: Any]]()
    private var memoryPageArray = [String]()
    private var memoryArray = [[String: Any]]()
    private var fpsPageArray = [String]()
    private var fpsArray = [[String: Any]]()
    private var networkPageArray = [String]()
    private var networkArray = [[String: Any]]()
    private var blockArray = [[String: Any]]()
    private var subThreadUIArray = [String]()
    private var uiLevelArray = [String]()
    private var leakArray = [String]()
    private var pageLoadArray = [[String: String]]()
    private var bigFileArray = [String]()
    private var pageEnterMap = [String: Any]()
    
    private let semaphore = DispatchSemaphore(value: 1)
}

// MARK: - Public
extension HealthManager {
    public func rebootAppForHealthCheck() {
        CacheManager.shared.saveHealthStart(true)
        CacheManager.shared.saveStartTimeSwitch(true)
        // ...DoraemonMethodUseTimeManager
        CacheManager.shared.saveNetFlowSwitch(true)
        CacheManager.shared.saveSubThreadUICheckSwitch(true)
        CacheManager.shared.saveMemoryLeak(true)
        //...dispatch_after
    }
    
    public func startHealthCheck() {
        start = true
        if start && !(secondTimer != nil) {
            secondTimer = Timer.init(timeInterval: 0.5, target: self, selector: #selector(doSecondFunction), userInfo: nil, repeats: true)
            RunLoop.current.add(secondTimer ?? Timer(), forMode: RunLoop.Mode.common)
            //...fpsUtil
        }
        //...DoraemonANRManager
        //...DoraemonUIProfileManager
    }
    
    public func stopHealthCheck() {
        start = false
        HealthCountdownWindow.shared.hide()
        CacheManager.shared.saveHealthStart(false)
        CacheManager.shared.saveStartTimeSwitch(false)
        //...DoraemonMethodUseTimeManager
        CacheManager.shared.saveNetFlowSwitch(false)
        CacheManager.shared.saveSubThreadUICheckSwitch(false)
        CacheManager.shared.saveMemoryLeak(false)
        //...DoraemonANRManager
        //...DoraemonUIProfileManager
        if secondTimer != nil {
            secondTimer?.invalidate()
        }
        //...fpsUtil
        self.upLoadData()
    }
    
    public func startEnterPage(vcClass: AnyClass) {
        if !start {
            return
        }
        
        if self.blackList(vcClass: vcClass) {
            return
        }
        
        let pageName = NSStringFromClass(vcClass)
        let beginTime = CACurrentMediaTime()
        pageEnterMap[pageName] = beginTime
    }
    
    public func enterPage(vcClass: AnyClass) {
//        if !start {
//            return
//        }
//        if self.blackList(vcClass: vcClass) {
//            return
//        }
//        HealthCountdownWindow.shared.start(number: 10)
//        let pageName = NSStringFromClass(vcClass)
//        if pageEnterMap.keys.contains(pageName) {
//            let begintime = pageEnterMap[pageName] ?? 0
//            let endTime = CACurrentMediaTime()
//            let costTime = Int((endTime - begintime) * 1000 + 0.5) // 四舍五入 ms
//            pageLoadArray.append(
//                ["page": NSStringFromClass(vcClass),
//                 "time": "\(costTime)"])
//        }
//        pageEnterMap.removeValue(forKey: pageName)
//        cpuArray.removeAll()
//        memoryArray.removeAll()
//        fpsArray.removeAll()
//        networkArray.removeAll()
    }
    
    public func leavePage(vcClass: AnyClass) {
//        if !start {
//            return
//        }
//        if self.blackList(vcClass: vcClass) {
//            return
//        }
//        var pageName = NSStringFromClass(vcClass)
//        if h5UrlString?.count ?? 0 > 0 {
//            pageName = "\(pageName)(\(h5UrlString ?? ""))"
//        }
//
//        if networkPageArray.count > 0 {
//            networkArray.append(
//                ["page": pageName,
//                 "values": networkPageArray])
//        }
//
//        // cpu 内存 fps必须保证每一个页面运行10秒
//        if HealthCountdownWindow.shared.getCountDown() > 0 {
//            return
//        }
//
//        if cpuPageArray.count > 0 {
//            cpuArray.append(
//                ["page": pageName,
//                 "values": cpuPageArray])
//        }
//
//        if memoryPageArray.count > 0 {
//            memoryArray.append(
//                ["page": pageName,
//                 "values": memoryPageArray])
//        }
//
//        if fpsPageArray.count > 0 {
//            fpsArray.append(
//                ["page": pageName,
//                 "values": fpsPageArray])
//        }
//
//        HealthCountdownWindow.shared.hide()
    }
    
    public func addANRInfo(anrInfo: NSDictionary) {
//        if start {
//            blockArray.append(
//                ["page": self.currentTopVC,
//                 "blockTime": anrInfo["duration"] as Any,
//                 "detail": anrInfo["content"] as Any])
//        }
    }
    
    public func addSubThreadUI(info: NSDictionary) {
//        if start {
//            subThreadUIArray.append(
//                ["page": self.currentTopVC,
//                 "detail": info["content"] as Any])
//        }
    }
    
    public func addUILevel(info: NSDictionary) {
//        if start {
//            uiLevelArray.append(
//                ["page": self.currentTopVC,
//                 "level": info["level"] as Any,
//                 "detail": info["detail"] as Any])
//        }
    }
    
    public func addLeak(info: NSDictionary) {
//        if start {
//            let viewStack = info["viewStack"]
//            let retainCycle = info["retainCycle"]
//            let detail = String("retainCycle: \n%@ \n\n retainCycle : \n%@\n\n", viewStack, retainCycle)
//            uiLevelArray.append(
//                ["page": info["className"],
//                 "detail": detail])
//        }
    }
    
    public func openH5Page(h5Url: String) {
        //...
    }
    
    public func blackList(vcClass: AnyClass) -> Bool {
        //...
        return false
    }
}

extension HealthManager {
    @objc func doSecondFunction() {
        //...
    }
    
    @objc func upLoadData() {
        //...
    }
    
    func currentTopVC() -> NSString {
//        let vc = UIViewController.topViewControllerForKeyWindow()
//        let vcName = NSStringFromClass(vc?.classForCoder ?? AnyClass)
//        return vcName
        return ""
    }
}
