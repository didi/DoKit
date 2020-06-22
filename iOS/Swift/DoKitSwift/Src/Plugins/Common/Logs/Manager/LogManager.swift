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
    let switchKey = "doraemon_logSwitchKey"
    var isOn = false
    var logs:[LogModel]
    private override init() {
        isOn = UserDefaults.standard.bool(forKey: switchKey)
        logs = [LogModel]()
    }

    func start() {
        isOn = true;
        rebindPrintMethod()
        UserDefaults.standard.set(isOn, forKey: switchKey)
    }
    func stop() {
        isOn = false
        bindPrintMethod()
        UserDefaults.standard.set(isOn, forKey: switchKey)
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




