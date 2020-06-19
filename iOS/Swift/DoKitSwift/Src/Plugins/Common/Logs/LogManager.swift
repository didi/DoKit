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
        print("LogManager init")
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

    @_silgen_name("addLogFromSwift")
    func addLog(log:UnsafePointer<CChar>) {
        let log = String(cString: log)
//        let model = LogModel.init()
//        model.content = log;
//        model.timestamp = Date.init()
//        logs.append(model);
     }
    
    public func clearLog() {
        let model = LogModel.init()
        model.content = "log";
        model.timestamp = Date.init()
        logs.append(model);
//        logs.removeAll()
    }

//    lazy var logs = { () -> [LogModel] in
//        let logs = [LogModel]()
//        return logs
//    }()
}




