//
//  HealthStartingTitle.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthStartingTitle: UIView {

    var titleLabel: UILabel!
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        titleLabel = UILabel(frame: CGRect(x: 0, y: 0, width: width, height: height))
        titleLabel.textColor = UIColor.hexColor(0x27BCB7)
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        titleLabel.textAlignment = .center
        self.addSubview(titleLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func renderUIWithTitle(_ title: String?) {
        if title != nil {
            titleLabel.text = title
        }
    }
    
    func showUITitle(show: Bool) {
        titleLabel.isHidden = !show
    }
}
