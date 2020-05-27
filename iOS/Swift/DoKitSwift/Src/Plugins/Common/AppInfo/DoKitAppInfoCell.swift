//
//  DoKitAppInfoCell.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation
import SnapKit

class DoKitAppInfoCell: UITableViewCell {
    var titleLabel:UILabel?
    var contentLabel:UILabel?
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        titleLabel = UILabel.init()
        titleLabel?.font = .systemFont(ofSize: 15)
        contentView.addSubview(titleLabel!)
        titleLabel?.snp.makeConstraints({ (make) in
            make.left.equalTo(contentView).offset(16)
            make.centerY.equalTo(contentView)
        })
        
        contentLabel = UILabel.init()
        contentLabel?.font = .systemFont(ofSize: 13)
        contentLabel?.textColor = .gray
        contentView.addSubview(contentLabel!)
        contentLabel?.snp.makeConstraints({ (make) in
            make.left.greaterThanOrEqualTo(titleLabel!.snp_right).offset(10)
            make.right.equalTo(contentView).offset(-16)
            make.centerY.equalTo(contentView)
        })
    }
}
