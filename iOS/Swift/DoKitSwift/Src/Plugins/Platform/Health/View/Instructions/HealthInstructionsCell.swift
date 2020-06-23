//
//  HealthInstructionsCell.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthInstructionsCell: UITableViewCell {
    
    var titleLabel = UILabel()
    var bgImg = UIImageView()
    var itemLabel = UILabel()
    var padding: CGFloat = 0.0
    var attributes = [NSAttributedString.Key: NSMutableParagraphStyle]()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        padding = kSizeFrom750_Landscape(32)
        bgImg = UIImageView.init(frame: CGRect.init(x: padding, y: 0, width: kSizeFrom750_Landscape(108), height: kSizeFrom750_Landscape(48)))
        bgImg.image = UIImage.init(named: "doraemon_health_cell_bg")
        
        titleLabel = UILabel.init(frame: CGRect.init(x: padding, y: bgImg.top+bgImg.height/8, width: bgImg.width, height: padding))
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        
        itemLabel = UILabel.init(frame: CGRect.init(x: padding, y: bgImg.bottom + padding / 2, width: kScreenWidth - padding * 2, height: padding))
        itemLabel.numberOfLines = 0
        itemLabel.textColor = UIColor.black_1()
        itemLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = kSizeFrom750_Landscape(8)
        paragraphStyle.alignment = NSTextAlignment.left
        attributes[NSAttributedString.Key.paragraphStyle] = paragraphStyle
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
}

// MARK: - Public
extension HealthInstructionsCell {
    func renderUI(title: String, itemLabel: String) -> CGFloat {
        self.titleLabel.text = title
        
        self.itemLabel.attributedText = NSAttributedString.init(string: itemLabel, attributes: self.attributes)
        self.itemLabel.sizeToFit()
        self.itemLabel.frame = CGRect.init(x: self.itemLabel.frame.origin.x, y: self.itemLabel.top, width: self.itemLabel.width, height: self.itemLabel.height)
        return self.itemLabel.bottom + padding * 2
    }
}

