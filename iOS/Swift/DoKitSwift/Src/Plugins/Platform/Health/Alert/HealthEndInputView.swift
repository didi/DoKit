//
//  HealthEndInputView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthEndInputView: UIView {
    
    public var label = UILabel()
    public var textField = UITextField()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        let padding = kSizeFrom750_Landscape(40)
        self.label = UILabel.init(frame: CGRect.init(x: padding, y: 0, width: self.width - padding * 2, height: padding + kSizeFrom750_Landscape(5)))
        self.label.textAlignment = NSTextAlignment.left
        self.label.textColor = UIColor.black_3()
        self.label.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24))
        
        self.textField = UITextField.init(frame: CGRect.init(x: padding, y: self.label.bottom, width: self.width - padding * 2, height: padding * 2))
        self.textField.textAlignment = NSTextAlignment.left
        self.textField.font = self.label.font
        self.textField.layer.borderColor = UIColor.black_1().cgColor
        self.textField.borderStyle = UITextField.BorderStyle.roundedRect  
        self.textField.layer.cornerRadius = kSizeFrom750_Landscape(8)
        
        self.addSubview(self.label)
        self.addSubview(self.textField)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

//MARK: - Public
extension HealthEndInputView {
    public func renderUI(tip: String, placeholder: String) {
        self.label.text = tip
        self.textField.placeholder = placeholder
    }
}
