//
//  HealthInstructionsCell.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthInstructionsCell: UITableViewCell {
    
    static let identifier = "DoraemonHealthInstructionsCell"
        
    private lazy var titleLabel: UILabel = {
        $0.textAlignment = .center
        $0.textColor = UIColor.white
        $0.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        return $0
    }(UILabel())
    
    private lazy var bgImg: UIImageView = {
        $0.image = DKImage(named: "doraemon_health_cell_bg")
        return $0
    }(UIImageView())
    
     private lazy var itemLabel: UILabel = {
        $0.numberOfLines = 0
        $0.textColor = UIColor.black_1()
        $0.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        return $0
    }(UILabel())
    
    var attributes = [NSAttributedString.Key: NSMutableParagraphStyle]()

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        
        self.selectionStyle = .none
                    
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = kSizeFrom750_Landscape(8)
        paragraphStyle.alignment = NSTextAlignment.left
        attributes[NSAttributedString.Key.paragraphStyle] = paragraphStyle
        
        self.addSubview(bgImg)
        self.addSubview(titleLabel)
        self.addSubview(itemLabel)
        
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        let padding = kSizeFrom750_Landscape(32)

        bgImg.frame = CGRect(x: padding, y: 0, width: kSizeFrom750_Landscape(108), height: kSizeFrom750_Landscape(48))
        titleLabel.frame = CGRect(x: padding, y: bgImg.top + bgImg.height/8, width: bgImg.width, height: padding)
        itemLabel.frame = CGRect(x: padding, y: bgImg.bottom + padding / 2, width: kScreenWidth - padding * 2, height: padding)
        self.frame = CGRect(x: self.left, y: self.top, width: self.width, height: itemLabel.bottom + padding)
    }
    
    override func awakeFromNib() {
        super.awakeFromNib()
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

    }
}

// MARK: - Public
extension HealthInstructionsCell {
    func renderUI(title: String, itemLabel: String) {
        self.titleLabel.text = title
        
        self.itemLabel.attributedText = NSAttributedString.init(string: itemLabel, attributes: self.attributes)
    }
}

