//
//  ColorPickInfoView.swift
//  AFNetworking
//
//  Created by 方林威 on 2020/5/29.
//

import UIKit

protocol ColorPickInfoViewDelegate: NSObjectProtocol {
    
}

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
        let image = UIImage.dynamic(with: UIImage("doraemon_close"), dark: UIImage("doraemon_close_dark"))
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
        if #available(iOS 13.0, *) {
            backgroundColor = UIColor.dynamic(with: .white, dark: .secondarySystemGroupedBackground)
        } else {
            backgroundColor = .white
        }
        layer.cornerRadius = kSizeFrom750_Landscape(8)
        layer.borderWidth = 1
        layer.borderColor = #colorLiteral(red: 0.6, green: 0.6, blue: 0.6, alpha: 0.2)
        
        addSubview(colorView)
        addSubview(valueLabel)
        addSubview(closeButton)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        let colorConst: CGFloat = kSizeFrom750_Landscape(28)
        colorView.frame = CGRect(
            x: kSizeFrom750_Landscape(32),
            y: height - colorConst,
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
        let image = UIImage.dynamic(with: UIImage("doraemon_close"), dark: UIImage("doraemon_close_dark"))
        closeButton.setImage(image, for: .normal)
    }
    
    override func touchesMoved(_ touches: Set<UITouch>, with event: UIEvent?) {
        super.touchesMoved(touches, with: event)
        guard let touch = touches.first else { return }
        
        let currentPoint = touch.location(in: self)
        // 获取上一个点
        let prePoint = touch.previousLocation(in: self)
        let offsetX = currentPoint.x - prePoint.x
        let offsetY = currentPoint.y - prePoint.y
        transform = transform.translatedBy(x: offsetX, y: offsetY)
    }
}

// MARK:- Public
extension ColorPickInfoView {
    
    func set(current hexValue: Int) {
        colorView.backgroundColor = UIColor.hexColor(hexValue)
        valueLabel.text = "#\(hexValue)"
    }
}

// MARK:- Actions
extension ColorPickInfoView {
    
    @objc
    private func closeAction() {
        NotificationCenter.default.post(name: .closePluginNotification, object: nil)
    }
}

