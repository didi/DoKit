//
//  CellSwitch.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/20.
//

import UIKit

protocol CellSwitchDelegate {
    func changeSwitchOn(on: Bool)
}

class CellSwitch: UIView {
    var delegate: CellSwitchDelegate?
    
    private var switchView = UISwitch()
    private var titleLabel = UILabel()
    private var topLine = UIView()
    private var downLine = UIView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setupUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func setupUI() {
        titleLabel.textColor = .black_1()
        titleLabel.font = .systemFont(ofSize: kSizeFrom750_Landscape(32))
        
        switchView.onTintColor = .blue()
        switchView.addTarget(self, action: #selector(switchChange(swh:)), for: .valueChanged)
        
        topLine.isHidden = true
        topLine.backgroundColor = .line()
        
        downLine.isHidden = true
        downLine.backgroundColor = .line()
        
        [titleLabel, switchView, topLine, downLine].forEach { addSubview($0) }
    }
    
    @objc func switchChange(swh: UISwitch) {
        delegate?.changeSwitchOn(on: swh.isOn)
    }
    
    func renderUIWithTitle(title: String, on: Bool) {
        titleLabel.text = title
        titleLabel.sizeToFit()
        titleLabel.frame.origin = CGPoint(x: 20, y: height/2 - titleLabel.height/2)
        
        switchView.isOn = on
        switchView.frame.origin = CGPoint(x: width - 20 - switchView.width, y: height/2 - switchView.height/2)
    }
    
    func needTopLine() {
        topLine.isHidden = false
        topLine.frame = CGRect(x: 0, y: 0, width: width, height: 0.5)
    }
    
    func needDownLine() {
        downLine.isHidden = false
        downLine.frame = CGRect(x: 0, y: height - 0.5, width: width, height: 0.5)
    }
}
