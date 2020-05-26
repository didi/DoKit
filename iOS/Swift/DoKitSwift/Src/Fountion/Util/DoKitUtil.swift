//
//  DoKitUtil.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import Foundation

class DoKitUtil {
    static func openAppSetting() {
        let url = URL(string: UIApplication.openSettingsURLString)
        if let url = url {
            if UIApplication.shared.canOpenURL(url) {
                if #available(iOS 10, *) {
                    UIApplication.shared.open(url, options: [:], completionHandler: nil)
                }else{
                    UIApplication.shared.openURL(url)
                }
                
            }
        }
    }
}
