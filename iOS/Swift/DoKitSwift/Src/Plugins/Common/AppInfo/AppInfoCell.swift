//
//  AppInfoCell.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

class AppInfoCell: UITableViewCell {
    var titleLabel:UILabel?
    var contentLabel:UILabel?
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        translatesAutoresizingMaskIntoConstraints = false
        
        titleLabel = UILabel.init()
        titleLabel?.font = .systemFont(ofSize: 15)
        titleLabel?.translatesAutoresizingMaskIntoConstraints = false;
        contentView.addSubview(titleLabel!)
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute: .leading, relatedBy: .equal, toItem: contentView, attribute: .leading, multiplier: 1, constant: 16))
        contentView.addConstraint(NSLayoutConstraint.init(item: titleLabel!, attribute: .centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1, constant: 0))
        
        contentLabel = UILabel.init()
        contentLabel?.font = .systemFont(ofSize: 13)
        contentLabel?.textColor = .gray
        contentLabel?.translatesAutoresizingMaskIntoConstraints = false;
        contentLabel?.setContentCompressionResistancePriority(.defaultLow, for: .horizontal)
        contentView.addSubview(contentLabel!)
        contentView.addConstraint(NSLayoutConstraint.init(item: contentLabel!, attribute: .leading, relatedBy: .greaterThanOrEqual, toItem: titleLabel, attribute: .trailing, multiplier: 1, constant: 10))
        contentView.addConstraint(NSLayoutConstraint.init(item: contentLabel!, attribute: .trailing, relatedBy: .equal, toItem: contentView, attribute: .trailing, multiplier: 1, constant: -16))
        contentView.addConstraint(NSLayoutConstraint.init(item: contentLabel!, attribute: .centerY, relatedBy: .equal, toItem: contentView, attribute: .centerY, multiplier: 1, constant: 0))
    }
}
