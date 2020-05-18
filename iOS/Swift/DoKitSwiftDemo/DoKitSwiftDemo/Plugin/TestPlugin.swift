//
//  TestPlugin.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import DoraemonKit

@objc(TestPlugin)
class TestPlugin: NSObject,DoraemonPluginProtocol {
    @objc func pluginDidLoad(){
        print("pluginDidLoad")
    }
}
