//
//  ToastUtil.swift
//  DoraemonKit-Swift
//   
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

class ToastUtil: NSObject {
    static func showToast(_ text: String, superView: UIView?) {
        if superView == nil {
            return
        }

        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(14.0))
        label.text = text
        label.sizeToFit()
        label.textColor = .black
        label.frame = CGRect(x: superView!.width / 2 - label.width / 2, y: superView!.height / 2 - label.height / 2, width: label.width, height: label.height)
        superView?.addSubview(label)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + Double(Int64(1.5 * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC), execute: {
            label.removeFromSuperview()
        })
    }
    
    static func showToastBlack(_ text: String, superView: UIView?) {
        if superView == nil {
            return
        }

        let label = UILabel()
        label.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
        let paragraphStyle = NSMutableParagraphStyle()
        paragraphStyle.lineSpacing = kSizeFrom750_Landscape(8)
        paragraphStyle.alignment = .center
        let attributes = [NSAttributedString.Key.paragraphStyle : paragraphStyle]
        label.attributedText = NSAttributedString(string: LocalizedString(text), attributes: attributes)
        label.numberOfLines = 0
        let size = label.sizeThatFits(CGSize(width: superView!.width - 50, height: CGFloat.greatestFiniteMagnitude))
        label.backgroundColor = UIColor.black
        let padding = kSizeFrom750_Landscape(37)
        label.frame = CGRect(x: superView!.width / 2 - size.width / 2 - padding, y: superView!.height / 2 - size.height / 2 - padding, width: size.width + padding * 2, height: size.height + padding * 2)
        label.layer.cornerRadius = kSizeFrom750_Landscape(8)
        label.layer.masksToBounds = true
        label.textColor = UIColor.white
        superView!.addSubview(label)
        DispatchQueue.main.asyncAfter(deadline: DispatchTime.now() + Double(Int64(1.5 * Double(NSEC_PER_SEC))) / Double(NSEC_PER_SEC), execute: {
            label.removeFromSuperview()
        })
    }
}

