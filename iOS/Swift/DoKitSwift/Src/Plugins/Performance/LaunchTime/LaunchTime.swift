//
//  LaunchTime.swift
//  DoraemonKit-Swift
//
//  Created by objc on 2020/5/29.
//

import UIKit

private var _internalLatency: TimeInterval = 0

struct LaunchTime {
    static fileprivate var startTime: TimeInterval = 0

    static var latency: TimeInterval {
        set {
            guard _internalLatency == 0, newValue > 0 else {
                return
            }
            let precision: Double = 1000000
            _internalLatency = floor(newValue*precision)/precision
        }

        get {
            return _internalLatency
        }
    }
}

extension LaunchTimePlugin {
    func onInstall() {
        LaunchTime.latency = CFAbsoluteTimeGetCurrent() - LaunchTime.startTime
    }
}

extension UIApplication {
    @objc dynamic static func applicationLoadHandle() {
        LaunchTime.startTime = CFAbsoluteTimeGetCurrent()
    }
}
