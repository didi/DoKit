//
//  BaseBigTitleView.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/26.
//

import UIKit

protocol BaseBigTitleViewDelegate {
    func bigTitleCloseClick()
}

class BaseBigTitleView: UIView {

    let titleLabel: UILabel
    var delegate: BaseBigTitleViewDelegate?
    var title: String {
        didSet{
            titleLabel.text = title
        }
    }
    override init(frame: CGRect) {
        titleLabel = UILabel()
        title = ""
        
        super.init(frame: frame)
        
        let offsetY = kIphoneStatusBarHeight
        let titleLabelOffsetY = offsetY + ((self.height-offsetY)/2 - kSizeFrom750_Landscape(67)/2)
        let closeBtnH = self.height - offsetY
        
        titleLabel.frame = CGRect(x: kSizeFrom750_Landscape(32), y: titleLabelOffsetY, width: self.width-kSizeFrom750_Landscape(32)-closeBtnH, height: kSizeFrom750_Landscape(67))
        titleLabel.textColor = UIColor.hexColor(0x324456)
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(48))
        self.addSubview(titleLabel)
        
        let closeImage = DKImage(named: "doraemon_close")
        let closeBtn = UIButton(frame: CGRect(x: self.width-closeBtnH, y: offsetY, width: closeBtnH, height: closeBtnH))
        closeBtn.imageView?.contentMode = .center
        closeBtn.setImage(closeImage, for: .normal)
        closeBtn.addTarget(self, action: #selector(closeClick), for: .touchUpInside)
        self.addSubview(closeBtn)
        
        let downLine = UIView(frame: CGRect(x: 0, y: self.height-kSizeFrom750_Landscape(1), width: self.width, height: kSizeFrom750_Landscape(1)))
        downLine.backgroundColor = UIColor.line
        self.addSubview(downLine)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc func closeClick() {
        delegate?.bigTitleCloseClick()
    }

}
