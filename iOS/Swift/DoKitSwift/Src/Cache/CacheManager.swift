//
//  CacheManager.swift
//  DoraemonKit-Swift
//   
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

private let kDoraemonH5historicalRecord = "doraemon_historical_record"

class CacheManager: NSObject {
    public static let shared = CacheManager()
    
    let defaults = UserDefaults.standard
    
    
    // MARK: - H5任意门历史记录
    public var h5historicalRecord: [String] {
        guard let array = defaults.object(forKey: kDoraemonH5historicalRecord) as? [String] else {
            return []
        }
        return array
    }
    
    public func saveH5historicalRecord(text: String?) {
        /// 过滤异常数据
        guard let recordString = text, !recordString.isBlack else { return }

        var records = h5historicalRecord
        records.insert(recordString, at: 0)

        /// 去重
        records.removeDuplicateIfNeeded()
 
        /// 限制数量
        if records.count > 10 {
            records.removeLast()
        }
        defaults.setValue(records, forKey: kDoraemonH5historicalRecord)
    }
    
    public func clearAllH5historicalRecord() {
        defaults.removeObject(forKey: kDoraemonH5historicalRecord)
    }

    public func clearH5historicalRecord(text: String?) {
        /// 过滤异常数据
        guard let recordString = text, !recordString.isBlack else { return }

        var records = h5historicalRecord
        records.removeAll { $0 == recordString }

        if records.isEmpty {
            defaults.removeObject(forKey: kDoraemonH5historicalRecord)
        } else {
            defaults.setValue(records, forKey: kDoraemonH5historicalRecord)
        }
    }
    
    public func saveHealthStart(_ on: Bool) {
        //...
    }
    
    public func saveStartTimeSwitch(_ on: Bool) {
        //...
    }
    
    public func saveNetFlowSwitch(_ on: Bool) {
        //...
    }
    
    public func saveSubThreadUICheckSwitch(_ on: Bool) {
        //...
    }
    
    public func saveMemoryLeak(_ on: Bool) {
        //...
    }
}
