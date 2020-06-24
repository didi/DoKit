//
//  CellButton.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/27.
//

import UIKit

protocol CellButtonDelegate {
    func cellBtnClick(sender: CellButton)
}

class CellButton: UIView {
    var delegate: CellButtonDelegate?
    var titleLabel: UILabel!
    var rightLabel: UILabel!
    var topLine: UIView!
    var downLine: UIView!
    var arrowImageView: UIImageView!
    
    override init(frame: CGRect) {
        titleLabel = UILabel()
        rightLabel = UILabel()
        topLine = UIView()
        downLine = UIView()
        arrowImageView = UIImageView(image: DKImage(named: "doraemon_more"))
        super.init(frame: frame)
        
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(32))
        titleLabel.textColor = UIColor.black_1
        self.addSubview(titleLabel)
        
        topLine.isHidden = true
        topLine.backgroundColor = UIColor.line
        self.addSubview(topLine)
        
        downLine.isHidden = true
        downLine.backgroundColor = UIColor.line
        self.addSubview(downLine)
        
        arrowImageView.frame = CGRect(x: self.width-kSizeFrom750_Landscape(32)-arrowImageView.width, y: self.height/2-arrowImageView.height/2, width: arrowImageView.width, height: arrowImageView.height)
        self.addSubview(arrowImageView)
        
        rightLabel.isHidden = true
        rightLabel.textColor = UIColor.black_2
        rightLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(32))
        self.addSubview(rightLabel)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(tapSelf))
        self.isUserInteractionEnabled = true
        self.addGestureRecognizer(tap)
        
    }
    
    func renderUIWithTitle(title: String) {
        titleLabel.text = title
        titleLabel.sizeToFit()
        titleLabel.frame = CGRect(x: 20, y: self.height/2-titleLabel.height/2, width: titleLabel.width, height: titleLabel.height)
    }
    
    func renderUIWithRightContent(rightContent: String) {
        rightLabel.isHidden = false
        rightLabel.text = rightContent
        rightLabel.sizeToFit()
        rightLabel.frame = CGRect(x: arrowImageView.left-kSizeFrom750_Landscape(24)-rightLabel.width, y: self.height/2-rightLabel.height/2, width: rightLabel.width, height: rightLabel.height)
    }
    
    func needTopLine() {
        topLine.isHidden = false
        topLine.frame = CGRect(x: 0, y: 0, width: self.width, height: 0.5)
    }
    
    func needDownLine() {
        downLine.isHidden = false
        downLine.frame = CGRect(x: 0, y: self.height-0.5, width: self.width, height: 0.5)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc func tapSelf(){
        delegate?.cellBtnClick(sender: self)
    }
    
}
