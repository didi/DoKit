//
//  HealthBgView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
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
    
}
