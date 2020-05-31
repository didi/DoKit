//
//  DoKiti18NUtil.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/25.
//

import Foundation

func LocalizedString(_ key: String) -> String{
    return  I18Util.localizedString(key)
}

class I18Util {
    class func localizedString(_ key : String) -> String {
        let language = Locale.preferredLanguages.first
        if let language = language {
            var fileNamePrefix = "zh-Hans"
            if language.hasPrefix("en") {
                fileNamePrefix = "en"
            }
            
            let bundle = Bundle(for: DoKit.self)
            let url = bundle.url(forResource: "DoKitSwift", withExtension: "bundle")
            if url == nil {
                return key
            }
            let urlBundle = Bundle(url: url!)
            let path: String? = urlBundle!.path(forResource: fileNamePrefix, ofType: "lproj")
            if let path = path {
                let bundle = Bundle.init(path: path)
                var localizedString = bundle?.localizedString(forKey: key, value: nil, table: "Doraemon")
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
