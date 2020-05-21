//
//  AppDelegate.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/11.
//  Copyright © 2020 didi. All rights reserved.
//

import Foundation
public class DoraemonManager: NSObject {
    public static let shareInstance = DoraemonManager()
    private override init() {}
    
    public func install() {
        print("dokit install")
    }

}

