//
//  LogSwitchStyleCell.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/12.
//

import UIKit
typealias switchBlock = (Bool) -> ()

class LogSwitchStyleCell: UITableViewCell {

    var titleLabel:UILabel?
    var switchButton:UISwitch?
    var switchOnBlock:switchBlock?
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    func initUI() {
        self.selectionStyle = .none
        titleLabel = UILabel.init()
        titleLabel?.font = .systemFont(ofSize: 17.0)
        titleLabel?.textColor = .black
        titleLabel?.translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(titleLabel!)
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute: .leading, relatedBy: .equal, toItem: contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute:.centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1.0, constant: 0))
        switchButton = UISwitch.init()
        switchButton?.translatesAutoresizingMaskIntoConstraints = false
        switchButton?.addTarget(self, action: #selector(self.switchStateChange(switchButton:)), for: .touchUpInside)
        contentView.addSubview(switchButton!)
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .leading, relatedBy: .equal, toItem: titleLabel, attribute: .trailing, multiplier: 1.0, constant: 10))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1.0, constant: -15))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 0.0, constant: 64))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute:.centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1.0, constant: 0))
    }
    @objc func switchStateChange(switchButton:UISwitch) {
        if (self.switchOnBlock != nil) {
            self.switchOnBlock!(switchButton.isOn)
        }
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
