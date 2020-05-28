//
//  ViewCheckView.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

class ViewCheckView: UIView {
    
    private let color = #colorLiteral(red: 1, green: 0, blue: 0, alpha: 1)
    private lazy var imageView = UIImageView(image: UIImage("doraemon_visual"))
    private lazy var borderView = UIView()
    
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
        
        let pan = UIPanGestureRecognizer(target: self, action: #selector(panAction))
        imageView.addGestureRecognizer(pan)
        imageView.isUserInteractionEnabled = true
        addSubview(imageView)
        
        defer { bringSubviewToFront(imageView) }
        
        borderView.isHidden = true
        borderView.layer.masksToBounds = true
        borderView.layer.borderColor = color.cgColor
        borderView.layer.borderWidth = 2
        addSubview(borderView)
    }
    
    override func point(inside point: CGPoint, with event: UIEvent?) -> Bool {
        return imageView.frame.contains(point)
    }
}

extension ViewCheckView {
    
    @objc
    private func panAction(_ sender: UIPanGestureRecognizer) {
        guard let view = sender.view else {
            return
        }
        // 更新图片位置
        let offset = sender.translation(in: view)
        sender.setTranslation(.zero, in: view)
        view.center = .init(
            x: max(0, min(width, view.center.x + offset.x)),
            y: max(0, min(height, view.center.y + offset.y))
        )
        // 获取当前点上除当前视图外的最上层的视图
        guard
            let subviews = window?.subviews(view.center),
            let current = subviews.filter({ $0 != self && !$0.isDescendant(of: self) }).last else {
            return
        }
        // 根据手势状态显示边框视图 并更新视图位置
        switch sender.state {
        case .began, .changed:
            borderView.frame = convert(current.bounds, from: current)
            borderView.isHidden = false
            
        default:
            borderView.isHidden = true
        }
    }
}

extension ViewCheckView {
    
    /// 重置位置
    func reset() {
        imageView.center = .init(x: width / 2, y: height / 2)
    }
}

fileprivate extension UIView {
    
    /// 获取某一点中的所有子视图
    /// - Parameter point: 点
    /// - Returns: 子视图
    func subviews(_ point: CGPoint) -> [UIView] {
        var views: [UIView] = []
        subviews.forEach { $0.recursion(into: &views, inside: convert(point, to: $0)) }
        return views
    }
    
    private func recursion(into views: inout [UIView], inside point: CGPoint) {
        guard self.point(inside: point, with: nil), !isHidden, alpha > 0 else {
            return
        }
        views.append(self)
        subviews.forEach {
            $0.recursion(into: &views, inside: convert(point, to: $0))
        }
    }
}

fileprivate extension UIView {
    
    var debugInfo: NSAttributedString {
        // 待完善
        return .init(string: "")
    }
}
