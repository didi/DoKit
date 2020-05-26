//
//  UIViewController+DoKit.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import Foundation
import UIKit

extension UIViewController {
    static func rootViewControllerForDoKitHomeWindow() -> UIViewController?{
        return DoKitHomeWindow.shared.rootViewController
    }
    
    static func rootViewControllerForKeyWindow() -> UIViewController?{
        return getKeyWindow()?.rootViewController
    }
    
    static func topViewControllerForKeyWindow() -> UIViewController?{
        let keyWindow = getKeyWindow()
        var vc = self._topViewControllerForKeyWindow(vc: keyWindow?.rootViewController)
        if vc == nil {
            return nil
        }
        if vc?.presentingViewController != nil {
            vc = self._topViewControllerForKeyWindow(vc: vc?.presentingViewController)
        }
        return vc
    }
    
    static func _topViewControllerForKeyWindow(vc:UIViewController?) -> UIViewController?{
        if let vc = vc {
            if vc is UINavigationController {
                let nav : UINavigationController = vc as! UINavigationController
                return self._topViewControllerForKeyWindow(vc: nav.topViewController)
            }else if vc is UITabBarController {
                let tab : UITabBarController = vc as! UITabBarController
                return self._topViewControllerForKeyWindow(vc: tab.selectedViewController)
            }else{
                return vc
            }
        }else{
            return nil
        }
    }
}
