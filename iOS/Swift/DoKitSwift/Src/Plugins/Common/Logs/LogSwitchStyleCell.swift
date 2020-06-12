//
//  LogSwitchStyleCell.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/12.
//

import UIKit

class LogSwitchStyleCell: UITableViewCell {

    var titleLabel:UILabel?
    var switchButton:UISwitch?
    
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
        titleLabel = UILabel.init()
        titleLabel?.font = .systemFont(ofSize: 15.0)
        titleLabel?.textColor = .white
        contentView.addSubview(titleLabel!)
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute: .leading, relatedBy: .equal, toItem: contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute:.centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1.0, constant: 0))
        
        switchButton = UISwitch.init()
        contentView.addSubview(switchButton!)
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .leading, relatedBy: .equal, toItem: titleLabel, attribute: .trailing, multiplier: 1.0, constant: 10))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1.0, constant: 10))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 0.0, constant: 64))
        contentView.addConstraint(NSLayoutConstraint.init(item: switchButton!, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 30))
    }
    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
