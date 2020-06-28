//
//  ANRManager.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

typealias ANRManagerBlock = ([String: Any]) -> (Void)

class ANRManager {
    
    static let sharedInstance = ANRManager()
    var anrTracker = ANRTracker()
    var timeout = 0.2
    private var block: ANRManagerBlock?
    
    @Store("doraemon_anr_track_key", defaultValue: false)
    var isOn: Bool
    
    init() {
        if isOn {
            start()
        } else {
            stop()
            // 如果是关闭的话，删除上一次的卡顿记录
            try? FileManager.default.removeItem(atPath: ANRTool.anrDirectory)
        }
    }
    
    deinit {
        anrTracker.stop()
    }

    func start() {
        anrTracker.start(threshold: timeout) {[weak self] (info) -> (Void) in
            self?.dump(info: info)
        }
    }
    
    func stop() {
        anrTracker.stop()
    }
    
    func addANRBlock(block: @escaping ANRManagerBlock) {
        self.block = block
    }
    
    private func dump(info: [String: Any]) {
        DispatchQueue.main.async {
            //TODO: [[DoraemonHealthManager sharedInstance] addANRInfo:info];
            self.block?(info)
            ANRTool.saveANRInfo(info: info)
        }
    }
}
