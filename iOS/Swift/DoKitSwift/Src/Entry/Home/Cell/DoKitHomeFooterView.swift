//
//  DoKitHomeFooterView.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import UIKit

class DoKitHomeFooterView: UICollectionReusableView {
    var titleLabel: UILabel!
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        titleLabel = UILabel()
        self.addSubview(titleLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        titleLabel.frame = CGRect(x: 0, y: 15, width: self.width, height: self.height-30)
    }
        
}
