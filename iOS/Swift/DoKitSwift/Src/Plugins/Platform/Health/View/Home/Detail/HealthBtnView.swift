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
    
    private lazy var healthBtn: UIImageView = {
        $0.image = DKImage(named: "doraemon_health_start")
        return $0
    }(UIImageView())
        
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.addSubview(healthBtn)
        
        let tapGesture = UITapGestureRecognizer(target: self, action: #selector(tapAction))
        self.addGestureRecognizer(tapGesture)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        healthBtn.frame = self.bounds
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
