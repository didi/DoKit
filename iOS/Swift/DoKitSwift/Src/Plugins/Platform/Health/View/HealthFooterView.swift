//
//  HealthFooterView.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

public protocol HealthFooterButtonDelegate {
    func footerBtnClick(sender: UIView)
}

class HealthFooterView: UIView {
    public var _top: Bool = false
    
    var footerTitle: UILabel?
    var footerImg: UIImageView?
    lazy var titleDictionary: NSDictionary = {
        return NSDictionary(dictionary: ["top": "向下滑动查看功能使用说明",
                                         "bottom": "回到顶部",
                                         "top_icon": "doraemon_health_slide",
                                         "bottom_icon": "doraemon_health_slideTop"])
    }()
    
    var delegate: HealthFooterButtonDelegate?

    override init(frame: CGRect) {
        super.init(frame: frame)
        
        footerTitle = UILabel(frame: CGRect(x: 0, y: 0, width: width, height: kSizeFrom750_Landscape(36)))
        footerTitle?.textAlignment = .center
        footerTitle?.textColor = UIColor.hexColor(0x27BCB7)
        footerTitle?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
        
        let size = kSizeFrom750_Landscape(56)
        footerImg = UIImageView(frame: CGRect(x: (width - size)/2, y: size, width: size, height: size))
        footerImg?.isUserInteractionEnabled = true
        
        self.addSubview(footerTitle!)
        self.addSubview(footerImg!)
        
        let tap = UITapGestureRecognizer(target: self, action: #selector(tapAction))
        footerImg?.addGestureRecognizer(tap)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func renderUIWithTitleImg(title: String, imgName: String) {
        footerTitle?.text = title
        footerImg?.image = DKImage(named: imgName)
    }
    
    func renderUIWithTitleImg(_ top: Bool) {
        var title: String
        var icon: String
        
        _top = top
        
        if top {
            title = self.titleDictionary["top"] as! String
            icon = self.titleDictionary["top_icon"] as! String
        } else {
            title = self.titleDictionary["bottom"] as! String
            icon = self.titleDictionary["bottom_icon"] as! String
        }
        
        footerTitle?.text = title
        footerImg?.image = DKImage(named: icon)
    }
    
    @objc func tapAction() {
        self.delegate?.footerBtnClick(sender: self)
    }
}
