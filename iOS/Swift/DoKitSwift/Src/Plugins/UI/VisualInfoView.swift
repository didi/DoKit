//
//  VisualInfoView.swift
//  DoraemonKit-Swift
//
//  Created by Lee on 2020/7/2.
//

import UIKit

extension VisualInfo {
    
    static let defalut = VisualInfoWindow<VisualInfoView>(frame: rect)
    
    private static var rect: CGRect {
        let height: CGFloat = kSizeFrom750_Landscape(100)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
        switch kOrientationPortrait {
        case true:
            return .init(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * margin,
                height: height
            )
            
        case false:
            return .init(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * margin,
                height: height
            )
        }
    }
}

class VisualInfoView: UIView {

    private lazy var valueLabel: UILabel = {
        $0.textColor = .black_1
        $0.font = .systemFont(ofSize: kSizeFrom750_Landscape(24))
        $0.adjustsFontSizeToFitWidth = true
        $0.minimumScaleFactor = 0.5
        $0.numberOfLines = 0
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
        let closeConst: CGFloat = kSizeFrom750_Landscape(44)
        closeButton.frame = CGRect(
            x: width - kSizeFrom750_Landscape(32) - closeConst,
            y: (height - closeConst) / 2,
            width: closeConst,
            height: closeConst
        )
        
        valueLabel.frame = CGRect(
            x: kSizeFrom750_Landscape(32),
            y: 0,
            width: closeButton.frame.minX - kSizeFrom750_Landscape(32) - kSizeFrom750_Landscape(10),
            height: height
        )
    }
    
    override func traitCollectionDidChange(_ previousTraitCollection: UITraitCollection?) {
        super.traitCollectionDidChange(previousTraitCollection)
        let image = DKImage(named: "doraemon_close")
        closeButton.setImage(image, for: .normal)
    }
}

// MARK: - Public
extension VisualInfoView {
    
    func set(text: String) {
        valueLabel.text = text
    }
}

// MARK: - Actions
extension VisualInfoView {
    
    @objc
    private func closeAction() {
        NotificationCenter.default.post(name: .closePluginNotification, object: nil)
    }
}

