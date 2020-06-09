//
//  ViewMetricsView.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

private var borderLayerKey: Void?

extension UIView {
    
    private var borderLayer: CALayer? {
        get { return objc_getAssociatedObject(self, &borderLayerKey) as? CALayer }
        set { objc_setAssociatedObject(self, &borderLayerKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC) }
    }
    
    @objc
    func metrics_layoutSubviews() {
        self.metrics_layoutSubviews()
        
        recursion(ViewMetrics.shared.enable)
    }
    
    private func recursion(_ enable: Bool) {
        guard let window = getKeyWindow(), isDescendant(of: window) else {
            return
        }
        
        subviews.forEach { $0.recursion(enable) }
        
        if enable {
            if borderLayer == nil {
                let border = CALayer()
                border.borderWidth = 1
                border.borderColor = UIColor.random.cgColor
                borderLayer = border
                layer.addSublayer(border)
            }
            borderLayer?.frame = bounds
            borderLayer?.isHidden = false
            
        } else {
            borderLayer?.isHidden = true
        }
    }
}
