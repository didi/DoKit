//
//  LogManager.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/18.
//

import UIKit
import fishhook
class LogManager: NSObject {
    static let shared = LogManager()
    var logs:[LogModel]
    
    @Store("doraemon_logSwitchKey", defaultValue: false)
    var isOn: Bool
    
    private override init() {
        logs = [LogModel]()
    }

    func start() {
        isOn = true;
        rebindPrintMethod()
    }
    func stop() {
        isOn = false
        bindPrintMethod()
    }
    public func clearLog() {
        logs.removeAll()
    }
    func addLog(log:String) {
        let model = LogModel.init()
        model.content = log;
        
        LogManager.shared.logs.append(model);
    }
}

@_silgen_name("addLogFromSwift")
func addLog(log:UnsafePointer<CChar>) {
    let log = String(cString: log)
    LogManager.shared.addLog(log: log)
 }




