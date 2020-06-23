//
//  HealthAlertView.swift
//  DoraemonKit-Swift
//
//  Created by 李盛安 on 2020/6/15.
//

import UIKit

typealias HealthAlertOKActionBlock = () -> Void
typealias HealthAlertCancelActionBlock = () -> Void
typealias HealthAlertQuitActionBlock = () -> Void

class HealthAlertView: UIView {
    
    var viewWidth: CGFloat = 0.0
    var viewHeight: CGFloat = 0.0
    var viewPadding: CGFloat = 0.0
    var titleLabel = UILabel()
    var alertView = UIView()
    var inputViewArray = [HealthEndInputView]()
    var okBtn = UIButton()
    var cancelBtn = UIButton()
    var quitBtn = UIButton()
    
    var okBlock: HealthAlertOKActionBlock!
    var cancelBlock: HealthAlertCancelActionBlock!
    var quitBlock: HealthAlertQuitActionBlock!
    
    init() {
        super.init(frame: UIScreen.main.bounds)
        self.backgroundColor = UIColor.init(white: 0.2, alpha: 0.6)
        self.isUserInteractionEnabled = true
        viewPadding = kSizeFrom750_Landscape(134)
        viewWidth = self.width - viewPadding * 2
        viewHeight = viewPadding
        alertView = UIView.init(frame: CGRect.init(x: viewPadding, y: viewHeight/5, width: viewWidth, height: viewHeight))
        alertView.backgroundColor = UIColor.white
        alertView.layer.cornerRadius = kSizeFrom750_Landscape(10)
        
        titleLabel = UILabel.init(frame: CGRect.init(x: 0, y: viewPadding/3, width: viewWidth, height: viewPadding/2))
        titleLabel.textAlignment = .center
        titleLabel.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        titleLabel.textColor = UIColor.black_1()
        
        okBtn = UIButton.init(frame: CGRect.init(x: viewWidth/3*2, y: 0, width: viewWidth/3, height: kSizeFrom750_Landscape(90)))
        okBtn.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        okBtn.layer.borderColor = UIColor.black_3().cgColor
        okBtn.layer.borderWidth = kSizeFrom750_Landscape(0.5)
        okBtn.layer.masksToBounds = true
        okBtn.setTitleColor(UIColor.black_3(), for: .normal)
        okBtn.setTitle(LocalizedString("确定") , for: .normal)
        okBtn.isEnabled = false
        okBtn.addTarget(self, action: #selector(okBtnAction(sender:)), for: .touchUpInside)
        
        quitBtn = UIButton.init(frame: CGRect.init(x: viewWidth/3, y: 0, width: viewWidth/3, height: kSizeFrom750_Landscape(90)))
        quitBtn.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        quitBtn.layer.borderColor = UIColor.black_3().cgColor
        quitBtn.layer.borderWidth = kSizeFrom750_Landscape(0.5)
        quitBtn.layer.masksToBounds = true
        quitBtn.setTitleColor(UIColor.black_3(), for: .normal)
        quitBtn.setTitle(LocalizedString("丢弃"), for: .normal)
        quitBtn.addTarget(self, action: #selector(quitBtnAction(sender:)), for: .touchUpInside)
        
        cancelBtn = UIButton.init(frame: CGRect.init(x: 0, y: 0, width: viewWidth/3, height: okBtn.height))
        cancelBtn.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        cancelBtn.layer.borderColor = UIColor.black_3().cgColor
        cancelBtn.layer.borderWidth = kSizeFrom750_Landscape(0.5)
        cancelBtn.clipsToBounds = true
        cancelBtn.setTitleColor(UIColor.black_3(), for: .normal)
        cancelBtn.setTitle(LocalizedString("取消"), for: .normal)
        cancelBtn.addTarget(self, action: #selector(cancelBtnAction(sender:)), for: .touchUpInside)
        
        self.addSubview(alertView)
        
        alertView.addSubview(titleLabel)
        alertView.addSubview(okBtn)
        alertView.addSubview(quitBtn)
        alertView.addSubview(cancelBtn)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

// MARK: - Public
extension HealthAlertView {
    public func renderUI(title: String, placeholders: [String], inputTips: [String], okText: String, quitText: String, cancelText: String, okBlock: @escaping HealthAlertOKActionBlock, quitBlock: HealthAlertQuitActionBlock, cancelBlock: @escaping HealthAlertCancelActionBlock) {
        var index = 0
        var placeHoleder = ""
        self.titleLabel.text = title
        for tip in inputTips {
            let inputView = HealthEndInputView.init(frame: CGRect.init(x: 0, y: viewHeight, width: viewWidth, height: viewPadding))
            if index < placeholders.count {
                placeHoleder = placeholders[index]
            } else {
                placeHoleder = ""
            }
            inputView.renderUI(tip: tip, placeholder: placeHoleder)
            inputView.textField.delegate = self
            alertView.addSubview(inputView)
            inputViewArray.append(inputView)
            viewHeight += viewPadding + kSizeFrom750_Landscape(10)
            index += 1
        }
        viewHeight += kSizeFrom750_Landscape(38)
        okBtn.frame = CGRect.init(x: okBtn.frame.origin.x, y: viewHeight, width: okBtn.width, height: okBtn.height)
        if okText.count > 0 {
            okBtn.setTitle(okText, for: .normal)
        }
        
        quitBtn.frame = CGRect.init(x: quitBtn.frame.origin.x, y: height, width: quitBtn.width, height: quitBtn.height)
        if quitText.count > 0 {
            quitBtn.setTitle(quitText, for: .normal)
        }
        
        cancelBtn.frame = CGRect.init(x: cancelBtn.frame.origin.x, y: viewHeight, width: cancelBtn.width, height: cancelBtn.height)
        if cancelText.count > 0 {
            cancelBtn.setTitle(cancelText, for: .normal)
        }
        
        viewHeight = okBtn.height
        alertView.frame = CGRect.init(x: viewPadding, y: alertView.frame.origin.y, width: viewWidth, height: viewHeight)
        
        self.okBlock = okBlock
        self.cancelBlock = cancelBlock
        self.quitBlock = cancelBlock
    }
    
    public func getInputText() -> [String] {
        var array = [String]()
        for inputView in inputViewArray {
            array.append(inputView.textField.text ?? "")
        }
        return array
    }
}

// MARK: - UITextFieldDelegate
extension HealthAlertView: UITextFieldDelegate {
    func textFieldDidEndEditing(_ textField: UITextField) {
        var enabled = true
        for inputView in inputViewArray where inputView.textField.text?.count ?? 0 <= 0 {
            enabled = false
        }
        okBtn.isEnabled = enabled
        if enabled {
            okBtn.setTitleColor(UIColor.black_1(), for: .normal)
        } else {
            okBtn.setTitleColor(UIColor.black_3(), for: .normal)
        }
    }
}

// MARK: - Action
extension HealthAlertView {
    @objc func okBtnAction(sender: UIButton) {
        guard okBlock != nil else {
            return
        }
        self.isHidden = true
    }
    
    @objc func quitBtnAction(sender: UIButton) {
        guard quitBlock != nil else {
            return
        }
        self.isHidden = true
    }
    
    @objc func cancelBtnAction(sender: UIButton) {
        guard cancelBlock != nil else {
            return
        }
        self.isHidden = true
    }
}
