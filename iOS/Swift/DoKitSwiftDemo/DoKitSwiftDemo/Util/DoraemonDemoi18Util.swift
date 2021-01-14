//
//  DoraemonKitDemoi18Util.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import Foundation
import UIKit

func DoraemonDemoLocalizedString(_ key: String) -> String{
    return  DoraemonDemoi18Util.localizedString(key)
}

class DoraemonDemoi18Util: NSObject {
    class func localizedString(_ key : String) -> String {
        let language = Locale.preferredLanguages.first
        if let language = language {
            var fileNamePrefix = "zh-Hans"
            if language.hasPrefix("en") {
                fileNamePrefix = "en"
            }
            
            let path: String? = Bundle.main.path(forResource: fileNamePrefix, ofType: "lproj")
            if let path = path {
                let bundle = Bundle.init(path: path)
                var localizedString = bundle?.localizedString(forKey: key, value: nil, table: "DoraemonKitDemo")
                if localizedString == nil {
                    localizedString = key
                }
                return localizedString!
            }else{
                return key
            }
            
        }else{
            return key
        }
    }
}
