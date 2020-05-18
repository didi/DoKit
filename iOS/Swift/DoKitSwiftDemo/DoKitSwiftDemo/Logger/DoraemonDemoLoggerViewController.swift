//
//  DoraemonDemoLoggerViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/15.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoLoggerViewController: DoraemonDemoBaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("日志测试Demo")
        
        let btn = UIButton(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: 60.0))
        btn.backgroundColor = UIColor.orange
        btn.setTitle(DoraemonDemoLocalizedString("添加一条NSLog日志"), for: .normal)
        btn.addTarget(self, action: #selector(addNSLog), for: .touchUpInside);
        view.addSubview(btn)
    }
    
    @objc func addNSLog() {
        let str = "jack"
        let age = 29
        NSLog("NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦NSLog日记来啦。。。str == %@  age == %zi", str,age)
        let specialString = "callnative://saveTian/%22saveTianDataCallback43%22"
        NSLog("%@", specialString)
    }

}
