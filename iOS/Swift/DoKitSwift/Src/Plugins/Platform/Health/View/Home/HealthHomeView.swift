//
//  HealthHomeView.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

typealias HealthHomeBlock = () -> Void

class HealthHomeView: UIView {
    
    private lazy var bgView: HealthBgView = {
        return $0
    }(HealthBgView())
    
    lazy var btnView: HealthBtnView = {
        $0.statusForBtn(start: HealthManager.shared.start)
        $0.delegate = self
        return $0
    }(HealthBtnView())
    
    lazy var startingTitle: HealthStartingTitle = {
        return $0
    }(HealthStartingTitle())
    
    var block: HealthHomeBlock?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        if HealthManager.shared.start {
            startingTitle.renderUIWithTitle(LocalizedString("正在检测中..."))
        } else {
            startingTitle.renderUIWithTitle(LocalizedString("点击开始检测"))
        }
                
        self.addSubview(bgView)
        self.addSubview(btnView)
        self.addSubview(startingTitle)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        bgView.frame = CGRect(x: 0, y: 0, width: width, height: height)
        btnView.frame = bgView.getButtonCGRect()
        startingTitle.frame = bgView.getStartingTitleCGRect()
    }
    
    func addBlock(block: @escaping HealthHomeBlock) {
        self.block = block
    }
    
    func _selfHandle() {
        self.block?()
    }
}

extension HealthHomeView: HealthButtonDelegate {
    func healthBtnClick(sender: UIView) {
        _selfHandle()
    }
}
