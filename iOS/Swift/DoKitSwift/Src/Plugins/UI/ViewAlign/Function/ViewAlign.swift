//
//  ViewAlign.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

class ViewAlign {
    
    static let shared = ViewAlign()
    
    private let view = ViewAlignView()
    private var observation: NSKeyValueObservation?
    
    init() {
        observation = DoraemonKit.getKeyWindow()?.observe(\.rootViewController, options: []) {
            [weak self] (observer, change) in
            guard let self = self else { return }
            observer.bringSubviewToFront(self.view)
        }
    }
    
    deinit {
        observation?.invalidate()
    }
}

extension ViewAlign {
    
    func show() {
        
    }
    
    func hide() {
        
    }
}
