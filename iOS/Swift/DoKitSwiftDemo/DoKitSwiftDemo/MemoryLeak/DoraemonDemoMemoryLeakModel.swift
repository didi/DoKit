//
//  DoraemonDemoMemoryLeakModel.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/19.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoMemoryLeakModel: NSObject {
    var closure: (()->Void)?
    
    func callClosure() {
        closure?()
    }
    
    deinit {
        print("model deinit")
    }

}
