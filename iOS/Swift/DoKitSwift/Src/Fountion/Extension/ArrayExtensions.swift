//
//  ArrayExtensions.swift
//  DoraemonKit-Swift
//   
//  Created by DeveloperLY on 2020/5/28.
//  
//

import Foundation

extension Array where Element: Hashable {
    /// 数组去重
    mutating func removeDuplicateIfNeeded() {
        if Set<Element>(self).count == count {
            return
        }

        var result = [Element]()
        for value in self {
            if !result.contains(value) {
                result.append(value)
            }
        }
        self = result
    }
}
