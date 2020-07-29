//
//  HealthStartingTitle.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthStartingTitle: UIView {
    
    private lazy var titleLabel: UILabel = {
        $0.textColor = UIColor.hexColor(0x27BCB7)
        $0.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        $0.textAlignment = .center
        return $0
    }(UILabel())
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.addSubview(titleLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        titleLabel.frame = self.bounds
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
