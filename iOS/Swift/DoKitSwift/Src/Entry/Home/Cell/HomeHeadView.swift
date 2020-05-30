//
//  HomeHeadView.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import UIKit

class HomeHeadView: UICollectionReusableView {
    var titleLabel: UILabel!
    var subTitleLabel: UILabel?
    override init(frame: CGRect) {
        super.init(frame: frame)
        titleLabel = UILabel()
        self.addSubview(titleLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func renderUI(title: String) {
        titleLabel.text = title
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
        if let subTitleLabel = subTitleLabel {
            subTitleLabel.removeFromSuperview()
        }
        if title.count>0 && title == LocalizedString("平台工具") {
            self.renderUI(subTitle: "(www.dokit.cn)")
        }
        self.setNeedsLayout()
        
    }
    
    func renderUI(subTitle: String) {
        if subTitle.count > 0 {
            if subTitleLabel == nil {
                subTitleLabel = UILabel()
            }
            self.addSubview(subTitleLabel!)
            subTitleLabel!.text = subTitle
            subTitleLabel!.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
            subTitleLabel?.textColor = UIColor.red
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        titleLabel.sizeToFit()
        titleLabel.frame = CGRect(x: kSizeFrom750_Landscape(32), y: (self.height-titleLabel.height)/2, width: titleLabel.width, height: titleLabel.height)
        if (subTitleLabel != nil) && subTitleLabel?.superview === self {
            subTitleLabel!.sizeToFit()
            subTitleLabel!.frame = CGRect(x: titleLabel.right+kSizeFrom750_Landscape(2), y: self.height/2-subTitleLabel!.height/2, width: subTitleLabel!.width, height: subTitleLabel!.height)
        }
        
    }
}
