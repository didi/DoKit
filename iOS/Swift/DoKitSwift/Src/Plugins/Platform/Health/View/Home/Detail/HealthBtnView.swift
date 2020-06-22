//
//  HealthBtnView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

public protocol HealthButtonDelegate {
    func healthBtnClick(sender: UIView)
}

class HealthBtnView: UIView {
    var delegate: HealthButtonDelegate?
    
    var healthBtn: UIImageView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        healthBtn = UIImageView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        healthBtn.image = DKImage(named: "doraemon_health_start")
        self.addSubview(healthBtn)
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(tapAction))
        self.addGestureRecognizer(tapGesture)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func statusForBtn(start: Bool) {
        var imgName = "doraemon_health_start"
        if start {
            imgName = "doraemon_health_end"
        }
        healthBtn.image = DKImage(named: imgName)
    }
    
    @objc func tapAction() {
        guard let delegate = self.delegate else { return }
        
        delegate.healthBtnClick(sender: self)
    }
}
