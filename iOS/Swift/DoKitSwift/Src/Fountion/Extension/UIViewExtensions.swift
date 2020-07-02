//
//  UIView+DoKit.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import Foundation
import UIKit

extension UIView {
    var left: CGFloat {
        get{
            return self.frame.origin.x
        }
        set(value){
            var rect = self.frame
            rect.origin.x = value
            self.frame = rect
        }
    }
    var top: CGFloat {
        get{
            return self.frame.origin.y
        }
        set(value){
            var rect = self.frame
            rect.origin.y = value
            self.frame = rect
        }
    }
    var width: CGFloat {
        get{
            return self.frame.size.width
        }
        set(value){
            var rect = self.frame
            rect.size.width = value
            self.frame = rect
        }
    }
    var height: CGFloat {
        get{
            return self.frame.size.height
        }
        set(value){
            var rect = self.frame
            rect.size.height = value
            self.frame = rect
        }
    }
    var bottom: CGFloat {
        get{
            return self.top + self.height
        }
        set(value){
            self.top = value - self.height
        }
    }
    var right: CGFloat {
        get{
            return self.left + self.width
        }
        set(value){
            self.left = value - self.width
        }
    }
    var centerX: CGFloat {
        get {
            return self.center.x
        }
        set(value) {
            self.center.x = value
        }
    }

    /// EZSE: getter and setter for the Y coordinate for the center of a view.
    var centerY: CGFloat {
        get {
            return self.center.y
        }
        set(value) {
            self.center.y = value
        }
    }
    
    var originX: CGFloat {
        get {
            self.frame.origin.x
        }
        set {
            self.frame.origin.x = newValue
        }
    }
    
    var originY: CGFloat {
        get {
            self.frame.origin.y
        }
        set {
            self.frame.origin.y = newValue
        }
    }
    
    var viewController: UIViewController? {
        var responder: UIResponder? = next
        while responder != nil {
            if let viewController = responder as? UIViewController {
                return viewController
            } else {
                responder = responder?.next
            }
        }

        return nil
    }
    
    func addSubViews(_ views: UIView...) {
        views.forEach { addSubview($0) }
    }
}
