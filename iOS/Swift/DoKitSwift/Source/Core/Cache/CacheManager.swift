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
    public var h5historicalRecord: [String]? {
        return defaults.object(forKey: kDoraemonH5historicalRecord) as? [String]
    }
    
    public func saveH5historicalRecord(text: String?) {
        /// 过滤异常数据
        if text?.isBlack ?? false {
            return
        }

        var records = h5historicalRecord
        
        if records != nil {
            records?.insert(text!, at: 0)
        } else {
            records = [text!]
        } 

        /// 去重
        records = records?.filterDuplicates({$0})
 
        /// 限制数量
        if records!.count > 10 {
            records!.removeLast()
        }
        defaults.setValue(records, forKey: kDoraemonH5historicalRecord)
    }
    
    public func clearAllH5historicalRecord() {
        defaults.removeObject(forKey: kDoraemonH5historicalRecord)
    }

    public func clearH5historicalRecord(text: String?) {
        /// 过滤异常数据
        if text?.isBlack ?? false {
            return
        }
        
        var records = h5historicalRecord
        
        /// 不包含
        if !records!.contains(text!) {
            return
        }
        
        records!.removeAll { $0 as String == text! }


        if records!.count > 0 {
            defaults.setValue(records, forKey: kDoraemonH5historicalRecord)
        } else {
            defaults.removeObject(forKey: kDoraemonH5historicalRecord)
        }
    }
}
