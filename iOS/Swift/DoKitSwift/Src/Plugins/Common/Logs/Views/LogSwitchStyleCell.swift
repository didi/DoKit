//
//  LogSwitchStyleCell.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/12.
//

import UIKit
typealias switchBlock = (Bool) -> ()

class LogSwitchStyleCell: UITableViewCell {
    static let identifier: String = "LogSwitchStyleCellID"
    var switchOnBlock:switchBlock?
    
    lazy var titleLabel: UILabel = {
        $0.font = .systemFont(ofSize: 17.0)
        $0.textColor = .black
        $0.translatesAutoresizingMaskIntoConstraints = false
        return $0
    }(UILabel())
    lazy var switchButton: UISwitch = {
        $0.translatesAutoresizingMaskIntoConstraints = false
        $0.addTarget(self, action: #selector(self.switchStateChange(switchButton:)), for: .touchUpInside)
        return $0
    }(UISwitch())

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func initUI() {
        self.selectionStyle = .none
        contentView.addSubview(titleLabel)
        contentView.addSubview(switchButton)

        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel, attribute: .leading, relatedBy: .equal, toItem: contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel, attribute:.centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1.0, constant: 0))
        
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton, attribute: .leading, relatedBy: .equal, toItem: titleLabel, attribute: .trailing, multiplier: 1.0, constant: 10))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1.0, constant: -15))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 0.0, constant: 64))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton, attribute:.centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1.0, constant: 0))
    }
    @objc func switchStateChange(switchButton:UISwitch) {
        switchOnBlock?(switchButton.isOn)
    }
    
}
