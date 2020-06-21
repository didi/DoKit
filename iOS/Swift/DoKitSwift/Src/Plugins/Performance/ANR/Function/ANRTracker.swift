//
//  ANRTracker.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

enum ANRTrackerStatus {
    case start  // 监控开启
    case stop   // 监控停止
}

class ANRTracker {
    var pingThread: PingThread?
    
    var status: ANRTrackerStatus {
        if pingThread != nil && pingThread?.isCancelled != true {
            return .start
        } else {
            return .stop
        }
    }
    
    func start(threshold: Double, handler: @escaping ANRManagerBlock) {
        pingThread = PingThread(threshold: threshold, handler: handler)
        pingThread?.start()
    }
    
    func stop() {
        if (pingThread != nil) {
            pingThread?.cancel()
            pingThread = nil
        }
    }
    
    deinit {
        pingThread?.cancel()
    }
}
