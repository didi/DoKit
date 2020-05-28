//
//  DoKitBasePlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import UIKit

public protocol Plugin {
    var module: String{get}
    var title: String{get}
    var icon: UIImage?{get}
    func didLoad()
}

struct DefaultPlugin: Plugin {
    var module: String
    var title: String
    var icon: UIImage?
    var callBack:()->Void
    
    func didLoad() {
        self.callBack()
    }
}
