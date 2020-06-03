//
//  DoKiti18NUtil.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import Foundation

func LocalizedString(_ key: String) -> String{
    guard let resourceBundle = sharedResourceBundle else {
        return key
    }

    return NSLocalizedString(key, tableName: nil, bundle: resourceBundle, comment: "")
}
