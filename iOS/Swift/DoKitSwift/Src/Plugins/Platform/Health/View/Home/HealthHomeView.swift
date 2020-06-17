//
//  HealthHomeView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

typealias HealthHomeBlock = () -> Void

class HealthHomeView: UIView {

    var btnView: HealthBtnView!
    var startingTitle: HealthStartingTitle!
    var bgView: HealthBgView!
    
    var block: HealthHomeBlock?
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        bgView = HealthBgView(frame: CGRect(x: 0, y: 0, width: width, height: height))
        
        // startingTitle 初始化
        startingTitle = HealthStartingTitle(frame: CGRect.zero)
        
        if true/*HealthManager.shared.start */ {
//            startingTitle.renderUIWithTitle(LocalizedString("正在检测中..."))
        } else {
//            startingTitle.renderUIWithTitle(LocalizedString("点击开始检测..."))
        }
        
        btnView = HealthBtnView(frame: CGRect.zero)
        btnView.statusForBtn(start: true/*HealthManager.shared.start */)
        btnView.delegate = self
        
        self.addSubview(bgView)
        self.addSubview(startingTitle)
        self.addSubview(btnView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
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
