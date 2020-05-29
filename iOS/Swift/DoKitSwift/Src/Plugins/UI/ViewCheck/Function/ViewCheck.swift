//
//  ViewCheck.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

class ViewCheck {
    
    static let shared = ViewCheck()
    
    private weak var view: ViewCheckView?
    private var observation: NSKeyValueObservation?
    
    private init() {
        observation = getKeyWindow()?.observe(\.rootViewController, options: []) {
            [weak self] (observer, change) in
            guard let self = self else { return }
            guard let view = self.view else { return }
            observer.bringSubviewToFront(view)
        }
        
        NotificationCenter.default.addObserver(
            forName: .init("DoraemonClosePluginNotification"),
            object: self,
            queue: .main
        ) { [weak self] (sender) in
            self?.hide()
        }
    }
    
    deinit {
        observation?.invalidate()
    }
}

extension ViewCheck {
    
    func show() {
        guard let window = getKeyWindow() else {
            return
        }
        
        if let view = view {
            window.bringSubviewToFront(view)
            view.reset()
            
        } else {
            let view = ViewCheckView()
            view.frame = window.bounds
            window.addSubview(view)
            view.reset()
            self.view = view
        }
    }
    
    func hide() {
        view?.removeFromSuperview()
    }
}
