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
        btn.setTitle(DoraemonDemoLocalizedString("添加一条print日志"), for: .normal)
        btn.addTarget(self, action: #selector(addNSLog), for: .touchUpInside);
        view.addSubview(btn)
    }
    
    @objc func addNSLog() {
        for num in 1...100 {
            DispatchQueue.global().async {
                print("北京欢迎你aaaaasdfsdfg欢迎你*^(*&R()8y23rkvwd예사소리/평음北京欢迎你aaaaasdfsdfg欢迎你*^(*&R()8y23rkvwd예사소리/평음jdfsjkhkjsdndsnkmvn\(num)")
//                print("thread:\(Thread.current)")
            }
        }
    }

}
