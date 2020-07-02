//
//  AppInfoUtil.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

struct AppInfoUtil {
    static var isIpad: Bool {
        return UIDevice.current.model.elementsEqual("iPad")
    }
}
