//
//  ViewAlignView.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

class ViewAlignView: UIView {
    
    private let color = #colorLiteral(red: 1, green: 0, blue: 0, alpha: 1)
    private lazy var imageView = UIImageView(image: UIImage.dokitImageNamed(name: "doraemon_visual"))
    private lazy var lines = (horizontal: UIView(), vertical: UIView())
    private lazy var labels = (top: UILabel(), left: UILabel(), bottom: UILabel(), right: UILabel())
    
    override func awakeFromNib() {
        super.awakeFromNib()
        setup()
    }
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
    required init?(coder: NSCoder) {
        super.init(coder: coder)
    }
    
    private func setup() {
        backgroundColor = .clear
        layer.zPosition = .greatestFiniteMagnitude
        
        let pan = UIPanGestureRecognizer(target: self, action: #selector(panAction))
        imageView.addGestureRecognizer(pan)
        addSubview(imageView)
        
        defer { bringSubviewToFront(imageView) }
        
        [lines.0, lines.1].forEach {
            $0.backgroundColor = color
            addSubview($0)
        }
        
        [labels.0, labels.1, labels.2, labels.3].forEach {
            $0.font = .systemFont(ofSize: 12)
            $0.textColor = color
            addSubview($0)
        }
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        
        
    }
}

extension ViewAlignView {
    
    @objc
    private func panAction(_ sender: UIPanGestureRecognizer) {
        
        
    }
}

extension ViewAlignView {
    
    func show() {
        
    }
    
    func hide() {
        
    }
}
