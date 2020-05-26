//
//  DoKitBasePlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import UIKit

class DoKitBasePlugin: NSObject {
    
    required override init() {
        super.init()
    }
    
    func pluginDidLoad(){
        print("子类实现功能")
    }
}
