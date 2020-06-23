//
//  UIProfileWindow.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

import UIKit

class UIProfileWindow: UIWindow {
    
    static let shared = UIProfileWindow(frame: CGRect(x: 10, y: 65, width: kScreenWidth, height: kScreenHeight))
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }
      
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

extension UIProfileWindow {
    private func setupUI() {
        if #available(iOS 13.0, *) {
            for windowScene in UIApplication.shared.connectedScenes {
                guard let windowScene = windowScene as? UIWindowScene else {
                    continue
                }
                if windowScene.activationState == .foregroundActive {
                    break
                }
            }
        }
        backgroundColor = .white
        layer.borderWidth = 2
        layer.borderColor = UIColor.lightGray.cgColor
        windowLevel = .statusBar + UIWindow.Level(rawValue: 50).rawValue
        clipsToBounds = true
        let pan = UIPanGestureRecognizer(target: self, action: #selector(pan(sender:)))
        self.addGestureRecognizer(pan)
        let tap = UIPanGestureRecognizer(target: self, action: #selector(tap(sender:)))
        self.addGestureRecognizer(tap)
    }
    
    @objc func pan(sender: UIPanGestureRecognizer) {
        
    }
    
    @objc func tap(sender: UIPanGestureRecognizer) {
        
    }
}

