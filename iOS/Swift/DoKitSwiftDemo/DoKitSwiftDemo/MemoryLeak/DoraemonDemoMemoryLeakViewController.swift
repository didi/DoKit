//
//  DoraemonDemoMemoryLeakViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/19.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoMemoryLeakViewController: DoraemonDemoBaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let leakView = DoraemonDemoMemoryLeakView(frame: CGRect(x: 100, y: 200, width: 100, height: 200))
        view.addSubview(leakView)
    }
    
    deinit {
        print("vc deinit")
    }

}
