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
    var logs = [LogModel]()
    
    private override init() {
        print("LogManager init")
        isOn = UserDefaults.standard.bool(forKey: switchKey)
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
        let s = String(cString: log)
        let model = LogModel.init()
        model.content = s;
        model.timestamp = Date.init()
        logs.append(model);
     }
        

}




