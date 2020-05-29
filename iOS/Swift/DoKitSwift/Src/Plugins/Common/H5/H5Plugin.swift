//
//  H5Plugin.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

struct H5Plugin: Plugin {
    
    var module: String {
        return LocalizedString("常用工具")
    }
    
    var title: String {
        return LocalizedString("H5任意门")
    }
    
    var icon: UIImage? {
        return UIImage("doraemon_h5")
    }
    
    func didLoad() {
        let vc = H5ViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
}
