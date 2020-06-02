//
//  ViewMetrics.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

class ViewMetrics {

    static let shared = ViewMetrics()
    
    var enable: Bool = false {
        didSet { getKeyWindow()?.layoutSubviews() }
    }
    
    private init() {
        guard
            let original = class_getInstanceMethod(UIView.self, #selector(UIView.layoutSubviews)),
            let swizzled = class_getInstanceMethod(UIView.self, #selector(UIView.metrics_layoutSubviews)) else {
            return
        }
        method_exchangeImplementations(original, swizzled)
    }
}
