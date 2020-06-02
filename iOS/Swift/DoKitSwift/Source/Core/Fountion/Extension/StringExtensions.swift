//
//  StringExtensions.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import Foundation

extension String {
    var isBlack: Bool {
        let trimmedStr = self.trimmingCharacters(in: .whitespacesAndNewlines)
        return trimmedStr.isEmpty
    }
}
