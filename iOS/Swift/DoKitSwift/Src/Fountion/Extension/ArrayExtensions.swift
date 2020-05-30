//
//  ArrayExtensions.swift
//  DoraemonKit-Swift
//   
//  Created by DeveloperLY on 2020/5/28.
//  
//

import Foundation

extension Array {
    /// 数组去重
    func filterDuplicates<E: Equatable>(_ filter: (Element) -> E) -> [Element] {
        var result = [Element]()
        for value in self {
            let key = filter(value)
            if !result.map({filter($0)}).contains(key) {
                result.append(value)
            }
        }
        return result
    }
}
