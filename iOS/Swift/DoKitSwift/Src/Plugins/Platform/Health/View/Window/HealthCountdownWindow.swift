//
//  HealthCountdownWindow.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthCountdownWindow: UIWindow {
    var showViewSize: CGFloat = 0.0
    var viewHeight: Int = 0
    var viewWidth: Int = 0
    var numberLabel = UILabel()
    var timer = Timer()
    var count: Int = 0
    
    static let shared = HealthCountdownWindow(frame: HealthCountdownWindow.rect)
     
    override init(frame: CGRect) {
        
        showViewSize = kSizeFrom750_Landscape(100)
        let x = kScreenWidth - showViewSize
        let y = kScreenHeight/5
        super.init(frame: CGRect.init(x: x, y: y, width: showViewSize, height: showViewSize))

        if #available(iOS 13.0, *) {
            for scene in UIApplication.shared.connectedScenes where scene.activationState == .foregroundActive {
                self.windowScene = windowScene
            }
        }
        
        numberLabel = UILabel.init(frame: CGRect.init(x: 0, y: 0, width: showViewSize, height: showViewSize))
        numberLabel.textColor = UIColor.black_1
        numberLabel.font = UIFont.systemFont(ofSize: showViewSize*2/5)
        numberLabel.textAlignment = .center
        self.addSubview(numberLabel)
        
        self.backgroundColor = UIColor.clear
        self.windowLevel = UIWindow.Level.statusBar + 50
        
        self.layer.cornerRadius = showViewSize/2
        self.layer.masksToBounds = true
        self.layer.borderWidth = showViewSize/20
        self.layer.borderColor = numberLabel.textColor.cgColor
        
        self.rootViewController = UIViewController.init()
        
        let move = UIPanGestureRecognizer.init(target: self, action: #selector(move(_:)))
        self.addGestureRecognizer(move)
    }
     
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

// MARK: - Public
extension HealthCountdownWindow {
    func start(number: Int) {
        self.isHidden = false
        count = number > 0 ? number : 10
        
        timer = Timer.scheduledTimer(timeInterval: 1.0, target: self, selector: #selector(handleTimer), userInfo: nil, repeats: true)
    }
    
    func hide() {
        self.isHidden = true
        timer.invalidate()
//        timer = nil
    }
    
    func getCountDown() -> Int {
        return 0
    }
}

// MARK: - Action
extension HealthCountdownWindow {
    @objc func move(_ sender: UIPanGestureRecognizer) {
        let offsetPoint = sender.translation(in: sender.view)
        sender.setTranslation(CGPoint.zero, in: sender.view)
        let panView = sender.view
        var newX = panView?.centerX ?? 0 + offsetPoint.x
        var newY = panView?.centerY ?? 0 + offsetPoint.y
        if newX < showViewSize / 2 {
            newX = showViewSize / 2
        }
        if newX > kScreenWidth - showViewSize / 2 {
            newX = showViewSize - showViewSize / 2
        }
        if newY < showViewSize / 2 {
            newY = showViewSize / 2
        }
        if newY > kScreenHeight - showViewSize / 2 {
            newY = kScreenHeight - showViewSize / 2
        }
        panView?.center = CGPoint.init(x: newX, y: newY)
    }
    
    @objc func handleTimer() {
        if self.count < 0 {
            self.hide()
        } else {
            self.numberLabel.text = "\(self.count)"
        }
        self.count -= 1
    }
}

// MARK: - Private
extension HealthCountdownWindow {
    
    static private var rect: CGRect {
        let height: CGFloat = kSizeFrom750_Landscape(100)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
        switch kOrientationPortrait {
        case true:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * margin,
                height: height
            )
            
        case false:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * margin,
                height: height
            )
        }
    }
}


