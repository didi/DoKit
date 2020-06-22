//
//  PingThread.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

/**
*  用于Ping主线程的线程类
*  通过信号量控制来Ping主线程，判断主线程是否卡顿
*/
class PingThread: Thread {
    
    /// 应用是否在活跃状态
    var isApplicationInActive: Bool = true
    
    /// 控制ping主线程的信号量
    var semaphore: DispatchSemaphore = DispatchSemaphore(value: 0)
    
    /// 卡顿阈值
    var threshold: Double = 0.0
    
    /// 卡顿回调
    var handler: ANRManagerBlock?
    
    /// 主线程是否阻塞
    var isMainThreadBlocked: Bool = false
    
    /// 判断是否需要上报
    var reportInfo: String = ""
    
    /// 每一次ping开始的时间,上报延迟时间统计
    var startTimeValue: Double = 0
    
    /// 初始化Ping主线程的线程类
    /// - Parameters:
    ///   - threshold: 主线程卡顿阈值
    ///   - handler: 监控到卡顿回调
    init(threshold: Double, handler: @escaping ANRManagerBlock) {
        self.threshold = threshold
        self.handler = handler
        super.init()
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidBecomeActve), name: UIApplication.didBecomeActiveNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(applicationDidEnterBackground), name: UIApplication.didEnterBackgroundNotification, object: nil)
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
    }
    
    override func main() {
        let verifyReport = {[weak self] in
            guard let reportInfo = self?.reportInfo, reportInfo.count > 0 else { return }
            let responseTimeValue = Date().timeIntervalSince1970
            let duration = (responseTimeValue - (self?.startTimeValue ?? 0))*1000
            self?.handler?([
                "title": DoKitUtil.dateFormatNow(),
                "duration": String(format: "%.0f", duration),
                "content": reportInfo
            ])
            self?.reportInfo = ""
        }
        
        while !isCancelled {
            if !isApplicationInActive {
                Thread.sleep(forTimeInterval: threshold)
            } else {
                isMainThreadBlocked = true
                reportInfo = ""
                startTimeValue = Date().timeIntervalSince1970
                DispatchQueue.main.async {
                    self.isMainThreadBlocked = false
                    verifyReport()
                    self.semaphore.signal()
                }
                Thread.sleep(forTimeInterval: threshold)
                if isMainThreadBlocked {
                    reportInfo = backtraceMainThread()
                }
                _ = semaphore.wait(timeout: DispatchTime.now() + 5)
                verifyReport()
            }
        }
    }
    
    @objc func applicationDidBecomeActve() {
        isApplicationInActive = true
    }
    
    @objc func applicationDidEnterBackground() {
        isApplicationInActive = false
    }
}
