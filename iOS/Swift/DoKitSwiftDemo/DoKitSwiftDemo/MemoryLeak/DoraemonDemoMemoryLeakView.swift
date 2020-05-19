//
//  DoraemonDemoMemoryLeakView.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/19.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoMemoryLeakView: UIView {
    var model : DoraemonDemoMemoryLeakModel
    override init(frame: CGRect) {
        model = DoraemonDemoMemoryLeakModel()
        super.init(frame: frame)
        self.backgroundColor = UIColor.orange
        model.closure = { ()->Void in
            self.doSomeThing()
        }
        model.callClosure()
    }
    
    func doSomeThing() {
        print("view doSomeThing");
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    deinit {
        print("view deinit")
    }
    
}
