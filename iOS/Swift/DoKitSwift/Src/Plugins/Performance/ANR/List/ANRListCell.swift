//
//  ANRListCell.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

class ANRListCell: UITableViewCell {
    
    var titleLabel = UILabel()
    var arrowImageView = UIImageView(image: DKImage(named: "doraemon_more"))
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none
        
        titleLabel.textColor = .black_1()
        titleLabel.font = .systemFont(ofSize: kSizeFrom750_Landscape(32))
        
        [titleLabel, arrowImageView].forEach { contentView.addSubview($0) }
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func renderCell(title: String) {
        titleLabel.text = title
        titleLabel.sizeToFit()
        let w = min(titleLabel.width, kScreenWidth - kSizeFrom750_Landscape(120))
        titleLabel.frame = CGRect(x: kSizeFrom750_Landscape(32), y: height/2 - titleLabel.height/2, width: w, height: titleLabel.height)
        arrowImageView.frame.origin = CGPoint(x: kScreenWidth - kSizeFrom750_Landscape(32) - arrowImageView.width, y: height/2 - arrowImageView.height/2)
    }

}
