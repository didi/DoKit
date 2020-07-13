//
//  HealthBgView.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

class HealthBgView: UIView {
    
    private lazy var bgImgView: UIImageView = {
        $0.image = DKImage(named: "doraemon_health_bg")
        return $0
    }(UIImageView())
    
    override init(frame: CGRect) {
        super.init(frame: frame)

        self.addSubview(bgImgView)
        self.isUserInteractionEnabled = false
        self.sendSubviewToBack(bgImgView)
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        let bg_x = kSizeFrom750_Landscape(98)
        let bg_width = width - bg_x*2
        bgImgView.frame = CGRect(x: bg_x, y: kSizeFrom750_Landscape(89), width: bg_width, height: bg_width*16/9)
    }
    
    /// 获取标题CGRect
    /// - Returns: CGRect
    func getStartingTitleCGRect() -> CGRect {
        var rect = CGRect()
        rect.size.width = self.width
        rect.size.height = kSizeFrom750_Landscape(40)
        rect.origin.x = 0
        rect.origin.y = bgImgView.top + bgImgView.height * 11 / 60
        return rect
    }
    
    func getButtonCGRect() -> CGRect {
        let point = CGPoint(x: bgImgView.left + bgImgView.width/2, y: bgImgView.top + bgImgView.height * 7 / 10)
        var rect = CGRect()
        rect.size.width = bgImgView.width * 2 / 5
        rect.size.height = rect.size.width
        rect.origin.x = point.x - rect.size.width / 2
        rect.origin.y = point.y - rect.size.width / 2 + kSizeFrom750_Landscape(5)
        return rect
    }
}
