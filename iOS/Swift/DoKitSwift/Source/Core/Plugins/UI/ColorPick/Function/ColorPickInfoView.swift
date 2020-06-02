//
//  ColorPickInfoView.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import UIKit

class ColorPickInfoView: UIView {

    private lazy var colorView: UIView = {
        $0.layer.borderWidth = 1
        $0.layer.borderColor = #colorLiteral(red: 0.6, green: 0.6, blue: 0.6, alpha: 0.2)
        return $0
    }(UIView())
    
    private lazy var valueLabel: UILabel = {
        $0.textColor = .black_1()
        $0.font = .systemFont(ofSize: kSizeFrom750_Landscape(28))
        return $0
    }(UILabel())
    
    private lazy var closeButton: UIButton = {
        let image = DKImage(named: "doraemon_close")
        $0.setBackgroundImage(image, for: .normal)
        $0.addTarget(self, action: #selector(closeAction), for: .touchUpInside)
        return $0
    }(UIButton())

    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        layer.cornerRadius = kSizeFrom750_Landscape(8)
        layer.borderWidth = 1
        layer.borderColor = #colorLiteral(red: 0.6, green: 0.6, blue: 0.6, alpha: 0.2)
        
        addSubview(colorView)
        addSubview(valueLabel)
        addSubview(closeButton)
        
        if #available(iOS 13.0, *) {
            backgroundColor = UIColor.dynamic(with: .white, dark: .secondarySystemGroupedBackground)
        } else {
            backgroundColor = .white
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        let colorConst: CGFloat = kSizeFrom750_Landscape(28)
        colorView.frame = CGRect(
            x: kSizeFrom750_Landscape(32),
            y: (height - colorConst) / 2,
            width: colorConst,
            height: colorConst
        )
        valueLabel.frame = CGRect(
            x: colorView.right + kSizeFrom750_Landscape(20),
            y: 0,
            width: kSizeFrom750_Landscape(150),
            height: height
        )
        
        let closeConst: CGFloat = kSizeFrom750_Landscape(44)
        closeButton.frame = CGRect(
            x: width - colorConst - kSizeFrom750_Landscape(32),
            y: (height - closeConst) / 2,
            width: closeConst,
            height: closeConst
        )
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let image = DKImage(named: "doraemon_close")
        closeButton.setImage(image, for: .normal)
    }
}

// MARK:- Public
extension ColorPickInfoView {
    
    func set(current color: UIColor) {
        colorView.backgroundColor = color
        valueLabel.text = color.hexString
    }
}

// MARK:- Actions
extension ColorPickInfoView {
    
    @objc
    private func closeAction() {
        NotificationCenter.default.post(name: .closePluginNotification, object: nil)
    }
}

