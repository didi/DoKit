//
//  HealthAlertView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

enum HealthAlertActionType: Int {
    case cancel = 1     // 取消
    case discard = 2    // 丢弃
    case submit = 3     // 提交
}

typealias HealthAlertActionBlock = (_ type: HealthAlertActionType) -> Void

class HealthAlertView: UIView {
    
    var viewWidth: CGFloat = 0.0
    var viewHeight: CGFloat = 0.0
    var viewPadding: CGFloat = 0.0
        
    private lazy var titleLabel: UILabel = {
        $0.textAlignment = .center
        $0.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        $0.textColor = UIColor.black_1()
        return $0
    }(UILabel())
    
    private lazy var alertView: UIView = {
        $0.backgroundColor = UIColor.white
        $0.layer.cornerRadius = kSizeFrom750_Landscape(10)
        return $0
    }(UIView())
    
    private lazy var okBtn: UIButton = {
        setUpMenuBtn($0)
        $0.isEnabled = false
        $0.tag = HealthAlertActionType.submit.rawValue
        $0.setTitle(LocalizedString("提交") , for: .normal)
        return $0
    }(UIButton())
    
    private lazy var cancelBtn: UIButton = {
        setUpMenuBtn($0)
        $0.tag = HealthAlertActionType.cancel.rawValue
        $0.setTitle(LocalizedString("取消"), for: .normal)
        return $0
    }(UIButton())
    
    private lazy var quitBtn: UIButton = {
        setUpMenuBtn($0)
        $0.tag = HealthAlertActionType.discard.rawValue
        $0.setTitle(LocalizedString("丢弃"), for: .normal)
        return $0
    }(UIButton())
    
    var inputViewArray = [HealthEndInputView]()
    
    var block: HealthAlertActionBlock!
    
    init() {
        super.init(frame: UIScreen.main.bounds)
        
        self.backgroundColor = UIColor.init(white: 0.2, alpha: 0.6)
        self.isUserInteractionEnabled = true
        
        self.addSubview(alertView)
        alertView.addSubview(titleLabel)
        alertView.addSubview(okBtn)
        alertView.addSubview(quitBtn)
        alertView.addSubview(cancelBtn)
        
        setUpUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
    }
}

// MARK: - Public
extension HealthAlertView {
    func setUpUI() {
        viewPadding = kSizeFrom750_Landscape(134)
        viewWidth = self.width - viewPadding * 2
        viewHeight = viewPadding
        alertView.frame = CGRect.init(x: viewPadding, y: self.height/5, width: viewWidth, height: kSizeFrom750_Landscape(200))
        titleLabel.frame = CGRect.init(x: 0, y: viewPadding/3, width: viewWidth, height: viewPadding/2)
        okBtn.frame = CGRect.init(x: viewWidth/3*2, y: 0, width: viewWidth/3, height: kSizeFrom750_Landscape(90))
        quitBtn.frame = CGRect.init(x: viewWidth/3, y: 0, width: viewWidth/3, height: kSizeFrom750_Landscape(90))
        cancelBtn.frame = CGRect.init(x: 0, y: 0, width: viewWidth/3, height: okBtn.height)

        var index = 0
        self.titleLabel.text = LocalizedString("结束前请完善下列信息")
        let inputTips =  [LocalizedString("测试用例名称"), LocalizedString("测试用例名称")]
        for tip in inputTips {
            let inputView = HealthEndInputView.init(frame: CGRect.init(x: 0, y: viewHeight, width: viewWidth, height: viewPadding))
            inputView.renderUI(tip: tip, placeholder: "")
            inputView.textField.delegate = self
            alertView.addSubview(inputView)
            inputViewArray.append(inputView)
            viewHeight += viewPadding + kSizeFrom750_Landscape(10)
            index += 1
        }
        viewHeight += kSizeFrom750_Landscape(38)
        okBtn.frame = CGRect.init(x: okBtn.frame.origin.x, y: viewHeight, width: okBtn.width, height: okBtn.height)

        quitBtn.frame = CGRect.init(x: quitBtn.frame.origin.x, y: viewHeight, width: quitBtn.width, height: quitBtn.height)

        cancelBtn.frame = CGRect.init(x: cancelBtn.frame.origin.x, y: viewHeight, width: cancelBtn.width, height: cancelBtn.height)

        viewHeight += okBtn.height
        alertView.frame = CGRect.init(x: viewPadding, y: alertView.frame.origin.y, width: viewWidth, height: viewHeight)
    }
    
    public func getInputText() -> [String] {
        var array = [String]()
        for inputView in inputViewArray {
            array.append(inputView.textField.text ?? "")
        }
        return array
    }
    
    func setUpMenuBtn(_ sender: UIButton) {
        sender.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30))
        sender.layer.borderColor = UIColor.black_3().cgColor
        sender.layer.borderWidth = kSizeFrom750_Landscape(0.5)
        sender.layer.masksToBounds = true
        sender.setTitleColor(UIColor.black_3(), for: .normal)
        sender.addTarget(self, action: #selector(btnAction(sender:)), for: .touchUpInside)
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
    @objc func btnAction(sender: UIButton) {
        guard block != nil else {
            return
        }
        self.isHidden = true
        switch sender.tag {
        case HealthAlertActionType.cancel.rawValue:
            block(HealthAlertActionType.cancel)
        case HealthAlertActionType.discard.rawValue:
            block(HealthAlertActionType.discard)
        case HealthAlertActionType.submit.rawValue:
            block(HealthAlertActionType.submit)
        default: break
        }
    }
}
