//
//  DoraemonDefine.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import Foundation
import UIKit

let kDoKitScreenWidth:CGFloat = UIScreen.main.bounds.size.width
let kDoKitScreenHeight:CGFloat = UIScreen.main.bounds.size.height
let kDoKitOrientationPortrait:Bool = UIApplication.shared.statusBarOrientation.isPortrait
let kIphoneNavBarHeight:CGFloat = kDoKitIsIphoneXSeries() ? 88 : 64
let kIphoneStatusBarHeight:CGFloat = kDoKitIsIphoneXSeries() ? 44 : 20
let kIphoneSafeBottomAreaHeight:CGFloat = kDoKitIsIphoneXSeries() ? 34 : 0
let kIphoneTopSensorHeight:CGFloat = kDoKitIsIphoneXSeries() ? 32 : 0


func kDoKitSizeFrom750(_ x: CGFloat) -> CGFloat {
    return x*kDoKitScreenWidth/750
}

func kDoKitkSizeFrom750_Landscape(_ x: CGFloat) -> CGFloat {
    if kDoKitOrientationPortrait {
        return kDoKitSizeFrom750(x)
    }else{
        return x*kDoKitScreenHeight/750
    }
}


func kDoKitIsIphoneXSeries() -> Bool{
    var iPhoneXSeries = false
    if UIDevice.current.userInterfaceIdiom != .phone {
        return iPhoneXSeries
    }
    
    if #available(iOS 11.0, *) {
        let mainWindow = getKeyWindow()
        if let mainWindow = mainWindow {
            if mainWindow.safeAreaInsets.bottom > 0 {
                iPhoneXSeries = true
            }
        }
    }
    
    return iPhoneXSeries
}

func getKeyWindow() -> UIWindow? {
    let keyWindow: UIWindow? = UIApplication.shared.delegate?.window ?? UIApplication.shared.windows.first
    return keyWindow
    
}

