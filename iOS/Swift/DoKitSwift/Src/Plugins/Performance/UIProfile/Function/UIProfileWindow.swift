//
//  UIProfileWindow.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

import UIKit


fileprivate let windowWidth = 220
fileprivate let expandHeight = 250
fileprivate let textHeight = 30

class UIProfileWindow: UIWindow {
    
    static let shared = UIProfileWindow(frame: CGRect(x: 10, y: 65, width: windowWidth, height: textHeight))
    
    var storedFrame = CGRect.zero
    
    lazy var textLb: UILabel = {
        let lb = UILabel(frame: CGRect(x: 0, y: 0, width: windowWidth, height: textHeight))
        lb.backgroundColor = .lightGray
        lb.textAlignment = .center
        return lb
    }()
    
    lazy var textView: UITextView = {
        let tv = UITextView(frame: CGRect(x: 0, y: textHeight, width: windowWidth, height: expandHeight - textHeight))
        tv.backgroundColor = .clear
        tv.textAlignment = .center
        tv.isEditable = false
        tv.isUserInteractionEnabled = false
        return tv
    }()
    

    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }
      
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func show(depthText: String, detailInfo: String) {
        textLb.text = depthText
        textView.text = detailInfo
        addSubview(textLb)
        addSubview(textView)
        isHidden = false
    }
    
    func hide() {
        textLb.removeFromSuperview()
        textView.removeFromSuperview()
        isHidden = true
    }
}

extension UIProfileWindow {
    private func setupUI() {
        if #available(iOS 13.0, *) {
            for windowScene in UIApplication.shared.connectedScenes {
                guard let windowScene = windowScene as? UIWindowScene else { continue }
                if windowScene.activationState == .foregroundActive { break }
            }
        }
        backgroundColor = .white
        layer.borderWidth = 2
        layer.borderColor = UIColor.lightGray.cgColor
        windowLevel = .statusBar + UIWindow.Level(rawValue: 50).rawValue
        clipsToBounds = true
        let pan = UIPanGestureRecognizer(target: self, action: #selector(onPan(sender:)))
        addGestureRecognizer(pan)
        let tap = UITapGestureRecognizer(target: self, action: #selector(onTap))
        addGestureRecognizer(tap)
    }
    
    @objc func onPan(sender: UIPanGestureRecognizer) {
        let offsetPoint = sender.translation(in: sender.view)
        sender.setTranslation(CGPoint.zero, in: sender.view)
        guard let panView = sender.view else { return }
        var newX = panView.centerX + offsetPoint.x
        var newY = panView.centerY + offsetPoint.y
        if newX < self.width/2 {
            newX = self.width/2
        }
        if newX > kScreenWidth - self.width/2 {
            newX = kScreenWidth - self.width/2
        }
        if newY < self.height/2 {
            newY = self.height/2
        }
        if newY > kScreenHeight - self.height/2 {
            newY = kScreenHeight - self.height/2
        }
        panView.center = CGPoint(x: newX, y: newY)
    }
    
    @objc func onTap() {
        if storedFrame.isEmpty {
            storedFrame = CGRect(x: originX, y: originY, width: width, height: 180)
        }
        UIView.animate(withDuration: 0.25) {
            (self.frame, self.storedFrame) = (self.storedFrame, self.frame)
        }
    }
}

