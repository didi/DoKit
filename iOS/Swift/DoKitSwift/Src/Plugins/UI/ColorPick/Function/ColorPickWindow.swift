//
//  ColorPickWindow.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import UIKit

fileprivate let windowSize: CGFloat = 150
class ColorPickWindow: UIWindow {
    
    static let shared = ColorPickWindow(frame:
        CGRect(x: kScreenWidth / 2 - windowSize / 2,
               y: kScreenHeight / 2 - windowSize / 2,
               width: windowSize,
               height: windowSize
        )
    )
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        renderUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func renderUI() {
        if kIsIphoneXSeries() {
            if #available(iOS 13, *) {
                if
                    let scene = UIApplication.shared.connectedScenes.first(where: { $0.activationState == .foregroundActive }),
                    let windowScene = scene as? UIWindowScene {
                    self.windowScene = windowScene
                }
            }
        }
        
        backgroundColor = .clear
        windowLevel = .statusBar + 1.1
        if rootViewController == nil {
            rootViewController = UIViewController()
        }
        
        let pan = UIPanGestureRecognizer(target: self, action: #selector(panAction))
        addGestureRecognizer(pan)
    }
}

extension ColorPickWindow {
    
    func show() {
        isHidden = false
    }
    
    func hide() {
        isHidden = true
    }
}

extension ColorPickWindow {
    
    @objc
    private func panAction(pan: UIPanGestureRecognizer) {
        
    }
    
}

extension ColorPickWindow {
    
}
