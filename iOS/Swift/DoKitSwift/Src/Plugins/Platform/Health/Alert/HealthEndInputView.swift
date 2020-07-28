//
//  HealthEndInputView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthEndInputView: UIView {
    
    public lazy var label: UILabel = {
        $0.textAlignment = NSTextAlignment.left
        $0.textColor = UIColor.black_3
        $0.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
        return $0
    }(UILabel())
    
    public lazy var textField: UITextField = {
        $0.textAlignment = NSTextAlignment.left
        $0.font = self.label.font
        $0.layer.borderColor = UIColor.black_1.cgColor
        $0.borderStyle = UITextField.BorderStyle.roundedRect
        $0.layer.cornerRadius = kSizeFrom750_Landscape(8)
        return $0
    }(UITextField())
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        self.addSubview(self.label)
        self.addSubview(self.textField)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        let padding = kSizeFrom750_Landscape(40)
        self.label.frame = CGRect.init(x: padding, y: 0, width: self.width - padding * 2, height: padding + kSizeFrom750_Landscape(5))
        self.textField.frame = CGRect.init(x: padding, y: self.label.bottom, width: self.width - padding * 2, height: padding * 2)
    }
}

//MARK: - Public
extension HealthEndInputView {
    public func renderUI(tip: String, placeholder: String) {
        self.label.text = tip
        self.textField.placeholder = placeholder
    }
}
