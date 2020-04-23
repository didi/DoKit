//
//  SwiftLog.swift
//  DoraemonKitDemo
//
//  Created by 邓锋 on 2020/4/23.
//  Copyright © 2020 yixiang. All rights reserved.
//

import Foundation
import UIKit

@objc class SwiftLog : NSObject{
    
    @objc class func addLog(log:String){
        print(CGRect.zero)
        print(self.classForCoder())
        print("zhe shi 一条带换行的日志   换行\n哈哈哈123456789嘿嘿")
        print("\n")
        print(log)
    }
}
