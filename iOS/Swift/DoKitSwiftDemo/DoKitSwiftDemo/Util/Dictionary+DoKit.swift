//
//  Dictionary+DoKit.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/14.
//  Copyright © 2020 didi. All rights reserved.
//

import Foundation

extension Dictionary {
    public func formatJson() -> String? {
        let jsonData = try? JSONSerialization.data(withJSONObject: self, options: JSONSerialization.WritingOptions())
        if let jsonData = jsonData {
            let jsonStr = String(data: jsonData, encoding: String.Encoding(rawValue: String.Encoding.utf8.rawValue))
            return jsonStr ?? ""
        }
        return nil
    }
    
    public static func constructFromJson (json: String) -> Dictionary? {
        let data = json.data(using: String.Encoding.utf8, allowLossyConversion: true)
        let obj = try? JSONSerialization.jsonObject(with: data!, options: JSONSerialization.ReadingOptions.mutableContainers)
        if let obj = obj {
            return obj as? Dictionary
        }
        return nil
    }
}
