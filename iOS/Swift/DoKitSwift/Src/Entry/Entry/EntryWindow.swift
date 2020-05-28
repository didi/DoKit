//
//  EntryWindow.swift
//  AFNetworking
//
//  Created by didi on 2020/5/22.
//

import UIKit

class EntryWindow: UIWindow {

    var entryBtn: UIButton
    
    override init(frame: CGRect) {
        entryBtn = UIButton()
        super.init(frame: frame)
        self.renderUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func show() {
        self.isHidden = false
    }
    
    private func renderUI() {
        self.windowLevel = .alert + 100
        if self.rootViewController == nil {
            self.rootViewController = UIViewController()
        }
        
        entryBtn.frame = self.bounds
        entryBtn.setImage(UIImage.dokitImageNamed(name: "doraemon_logo"), for: .normal)
        entryBtn.layer.cornerRadius = 20
        entryBtn .addTarget(self, action: #selector(entryClick(btn:)), for: .touchUpInside)
        self.rootViewController?.view.addSubview(entryBtn)
        
        let pan = UIPanGestureRecognizer(target: self, action: #selector(pan(pan:)))
        self.addGestureRecognizer(pan)
        
    }
    
    @objc private func entryClick(btn:UIButton) {
        if HomeWindow.shared.isHidden {
            HomeWindow.shared.show()
        }else{
            HomeWindow.shared.hide()
        }
    }
    
    @objc private func pan(pan:UIPanGestureRecognizer){
        let panView = pan.view
        let offsetPoint = pan.translation(in: panView)
        pan.setTranslation(CGPoint(x: 0, y: 0), in: panView)
        
        var newX = panView!.centerX + offsetPoint.x
        var newY = panView!.centerY + offsetPoint.y
        
        if newX < self.width/2 {
            newX = self.width/2
        }
        if newX > kScreenWidth - self.width/2 {
            newX = kScreenWidth - self.width/2
        }
        if newY < self.height/2 {
            newY = self.height/2
        }
        if newY > kScreenHeight - self.height/2{
            newY = kScreenHeight - self.height/2
        }
        
        panView?.center = CGPoint(x: newX, y: newY)
        
    }
}
