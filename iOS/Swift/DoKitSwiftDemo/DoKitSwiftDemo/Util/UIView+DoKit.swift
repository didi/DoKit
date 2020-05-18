//
//  DoraemonDemoUIViewExtension.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import Foundation
import UIKit

extension UIView {
    public var left: CGFloat {
        get{
            return self.frame.origin.x
        }
        set(value){
            var rect = self.frame
            rect.origin.x = value
            self.frame = rect
        }
    }
    public var top: CGFloat {
        get{
            return self.frame.origin.y
        }
        set(value){
            var rect = self.frame
            rect.origin.y = value
            self.frame = rect
        }
    }
    public var width: CGFloat {
        get{
            return self.frame.size.width
        }
        set(value){
            var rect = self.frame
            rect.size.width = value
            self.frame = rect
        }
    }
    public var height: CGFloat {
        get{
            return self.frame.size.height
        }
        set(value){
            var rect = self.frame
            rect.size.height = value
            self.frame = rect
        }
    }
    public var bottom: CGFloat {
        get{
            return self.top + self.height
        }
        set(value){
            self.top = value - self.height
        }
    }
    public var right: CGFloat {
        get{
            return self.left + self.width
        }
        set(value){
            self.left = value - self.width
        }
    }
}

