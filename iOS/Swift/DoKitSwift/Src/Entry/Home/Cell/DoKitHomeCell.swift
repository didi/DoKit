//
//  DoKitHomeCell.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import UIKit


class DoKitHomeCell: UICollectionViewCell {
    var iconView: UIImageView!
    var nameLabel: UILabel!
    override init(frame: CGRect) {
        super.init(frame:frame)
        let size = kSizeFrom750_Landscape(68)
        iconView = UIImageView()
        iconView.frame = CGRect(x: (frame.size.width-size)/2, y: 4, width: size, height: size)
        self.addSubview(iconView)
        
        let height = kSizeFrom750_Landscape(32)
        nameLabel = UILabel()
        nameLabel.frame = CGRect(x: 0, y: self.height-height-4, width: self.width, height: height)
        nameLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
        nameLabel.textAlignment = .center
        nameLabel.adjustsFontSizeToFitWidth = true
        self.addSubview(nameLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func update(name:String?, icon:UIImage?) {
        iconView.image = icon
        nameLabel.text = name
        
    }
    
}
