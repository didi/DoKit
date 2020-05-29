//
//  ColorPickView.swift
//  AFNetworking
//
//  Created by 方林威 on 2020/5/29.
//

import UIKit

class ColorPickView: UIView {

    private lazy var circleView: UIImageView = {
        $0.layer.masksToBounds = true
        $0.layer.borderWidth = 5
        $0.layer.backgroundColor = UIColor.clear.cgColor
        return $0
    }(UIImageView())
    
    private lazy var colorLabel: UILabel = {
        $0.backgroundColor = .clear
        $0.font = .systemFont(ofSize: 12)
        $0.textColor = .black
        $0.textAlignment = .center
        return $0
    }(UILabel())
    
    private lazy var pointView: UIView = {
        $0.backgroundColor = .black
        $0.layer.cornerRadius = 1.5
        return $0
    }(UIView())
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
        setupBezierPath()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        addSubview(circleView)
        addSubview(pointView)
        addSubview(colorLabel)
        
        circleView.layer.cornerRadius = width / 2
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        circleView.frame = CGRect(x: 0, y: 0, width: width, height: height)
        pointView.center = center
        pointView.frame.size = CGSize(width: 6, height: 6)
        colorLabel.frame = CGRect.init(x: 0, y: 15, width: width, height: 12)
    }
}

extension ColorPickView {
    
    func set(current image: UIImage) {
        circleView.image = image
    }
    
    func set(current color: UIColor) {
        circleView.layer.borderColor = color.cgColor
        colorLabel.text = color.hexString
    }
}

extension ColorPickView {
    
    private func setupBezierPath() {
        do {
            let path = UIBezierPath(ovalIn: CGRect(x: left - 0.5, y: top - 0.5, width: width + 1, height: height + 1))
            let layer = CAShapeLayer()
            layer.lineWidth = 1
            layer.strokeColor = UIColor.lightGray.cgColor
            layer.fillColor = UIColor.clear.cgColor
            layer.path = path.cgPath
            self.layer.addSublayer(layer)
        }
        
        do {
            let path = UIBezierPath(ovalIn: CGRect(x: left + 5, y: top + 5, width: width - 10, height: height - 10))
            let layer = CAShapeLayer()
            layer.lineWidth = 1
            layer.strokeColor = UIColor.lightGray.cgColor
            layer.fillColor = UIColor.clear.cgColor
            layer.path = path.cgPath
            self.layer.addSublayer(layer)
        }
    }
}
