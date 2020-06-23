//
//  HealthBgView.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthBgView: UIView {
    var bgImgView: UIImageView!
    
    override init(frame: CGRect) {
        super.init(frame: frame)

        let bg_x = kSizeFrom750_Landscape(98)
        let bg_width = width - bg_x*2
        bgImgView = UIImageView(frame: CGRect(x: bg_x, y: kSizeFrom750_Landscape(89), width: bg_width, height: bg_width*16/9))
        bgImgView.image = DKImage(named: "doraemon_health_bg")
        self.addSubview(bgImgView)
        self.isUserInteractionEnabled = false
        self.sendSubviewToBack(bgImgView)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func getStartingTitleCGRect() -> CGRect {
        return CGRect(origin: CGPoint(x: 0, y: bgImgView.frame.minY + bgImgView.height * 11/60), size: CGSize(width: self.width, height: kSizeFrom750_Landscape(40)))//根据图片比例获取)
    }
    
    func getButtonCGRect() -> CGRect {
        let width = bgImgView.width*2/5
        let point = CGPoint(x: bgImgView.frame.minX + bgImgView.width*2, y: bgImgView.frame.minY + bgImgView.height * 7/10)
        return CGRect(origin: CGPoint(x: point.x - width/2, y: point.y - width/2 + kSizeFrom750_Landscape(5)), size: CGSize(width: width, height: width))
    }
}
