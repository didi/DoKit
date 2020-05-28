//
//  Define.swift
//  AFNetworking
//
//  Created by didi on 2020/5/22.
//

import Foundation
import UIKit

let DoKitVersion:String = "0.0.1"
let kScreenWidth:CGFloat = UIScreen.main.bounds.size.width
let kScreenHeight:CGFloat = UIScreen.main.bounds.size.height
let kOrientationPortrait:Bool = UIApplication.shared.statusBarOrientation.isPortrait
let kIphoneNavBarHeight:CGFloat = kIsIphoneXSeries() ? 88 : 64
let kIphoneStatusBarHeight:CGFloat = kIsIphoneXSeries() ? 44 : 20
let kIphoneSafeBottomAreaHeight:CGFloat = kIsIphoneXSeries() ? 34 : 0
let kIphoneTopSensorHeight:CGFloat = kIsIphoneXSeries() ? 32 : 0


func kSizeFrom750(_ x: CGFloat) -> CGFloat {
    return x*kScreenWidth/750
}

func kSizeFrom750_Landscape(_ x: CGFloat) -> CGFloat {
    if kOrientationPortrait {
        return kSizeFrom750(x)
    }else{
        return x*kScreenHeight/750
    }
}


func kIsIphoneXSeries() -> Bool{
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


