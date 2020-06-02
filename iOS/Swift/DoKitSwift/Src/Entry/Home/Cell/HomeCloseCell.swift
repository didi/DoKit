//
//  HomeCloseCell.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import UIKit

class HomeCloseCell: UICollectionViewCell {
    var closeBtn: UIButton!
    override init(frame: CGRect) {
        super.init(frame: frame)
        closeBtn = UIButton(type: .custom)
        let x = kSizeFrom750_Landscape(10)
        closeBtn.frame = CGRect(x: x, y: 0, width: frame.width-x*2, height: kSizeFrom750_Landscape(100))
        closeBtn.backgroundColor = UIColor.white
        closeBtn.layer.cornerRadius = kSizeFrom750_Landscape(5.0)
        closeBtn.layer.masksToBounds = true
        closeBtn.setTitle(LocalizedString("关闭DoraemonKit"), for: .normal)
        closeBtn.setTitleColor(UIColor.hexColor(0xCC3A4B), for: .normal)
        closeBtn.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        closeBtn.addTarget(self, action: #selector(closeClick), for: .touchUpInside)
        
        self.addSubview(closeBtn)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    @objc private func closeClick() {
        HomeWindow.shared.hide()
        if let vc = UIViewController.rootViewControllerForKeyWindow() {
            AlertUtil.handleAlertAction(vc: vc, title: LocalizedString("提示"), text: "该功能需要重启App才能生效", ok: "确定", cancel: "取消", okBlock: {
                DoKit.shared.hideDoKit()
            }) {
                //print("cancel")
            }
        }else{
            DoKit.shared.hideDoKit()
        }

    }
    
}
