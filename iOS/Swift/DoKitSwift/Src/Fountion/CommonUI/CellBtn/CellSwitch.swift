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
    static let defaultHeight: CGFloat = 53
    
    var delegate: CellSwitchDelegate?
    
    private var switchView = UISwitch()
    private var titleLabel = UILabel()
    private var topLine = UIView()
    private var bottomLine = UIView()
    
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
        
        bottomLine.isHidden = true
        bottomLine.backgroundColor = .line()
        
        [titleLabel, switchView, topLine, bottomLine].forEach {
            $0.translatesAutoresizingMaskIntoConstraints = false
            addSubview($0)
        }
        
        let switchViewConstraints = [
            switchView.centerYAnchor.constraint(equalTo: centerYAnchor),
            switchView.trailingAnchor.constraint(equalTo: trailingAnchor, constant: -20)
        ]
        NSLayoutConstraint.activate(switchViewConstraints)
        
        let titleLabelConstraints = [
            titleLabel.leadingAnchor.constraint(equalTo: leadingAnchor, constant: 20),
            titleLabel.topAnchor.constraint(equalTo: topAnchor),
            titleLabel.bottomAnchor.constraint(equalTo: bottomAnchor),
            titleLabel.trailingAnchor.constraint(equalTo: switchView.leadingAnchor, constant: 10),
        ]
        NSLayoutConstraint.activate(titleLabelConstraints)
        
        let topLineConstraints = [
            topLine.leadingAnchor.constraint(equalTo: leadingAnchor),
            topLine.trailingAnchor.constraint(equalTo: trailingAnchor),
            topLine.topAnchor.constraint(equalTo: topAnchor),
            topLine.heightAnchor.constraint(equalToConstant: 0.5)
        ]
        NSLayoutConstraint.activate(topLineConstraints)
        
        let bottomLineConstraints = [
            bottomLine.leadingAnchor.constraint(equalTo: leadingAnchor),
            bottomLine.trailingAnchor.constraint(equalTo: trailingAnchor),
            bottomLine.bottomAnchor.constraint(equalTo: bottomAnchor),
            bottomLine.heightAnchor.constraint(equalToConstant: 0.5)
        ]
        NSLayoutConstraint.activate(bottomLineConstraints)
    }
    
    @objc func switchChange(swh: UISwitch) {
        delegate?.changeSwitchOn(on: swh.isOn)
    }
    
    func renderUIWithTitle(title: String, on: Bool) {
        titleLabel.text = title
        switchView.isOn = on
    }
    
    func needTopLine() {
        topLine.isHidden = false
    }
    
    func needDownLine() {
        bottomLine.isHidden = false

    }
}
