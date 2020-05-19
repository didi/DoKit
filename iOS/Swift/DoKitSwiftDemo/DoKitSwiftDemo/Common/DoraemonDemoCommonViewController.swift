//
//  DoraemonDemoCommonViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/19.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import DoraemonKit

class DoraemonDemoCommonViewController: DoraemonDemoBaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("通用测试Demo")
        
        let btn0 = UIButton(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: 60))
        btn0.backgroundColor = UIColor.orange
        btn0.setTitle("子线程UI操作", for: .normal)
        btn0.addTarget(self, action: #selector(addSubViewAtOtherThread), for: .touchUpInside)
        view.addSubview(btn0)
        
        let btn1 = UIButton(frame: CGRect(x: 0, y: btn0.bottom+20, width: view.width, height: 60))
        btn1.backgroundColor = UIColor.orange
        btn1.setTitle("显示入口", for: .normal)
        btn1.addTarget(self, action: #selector(showEntry), for: .touchUpInside)
        view.addSubview(btn1)
        
        let btn2 = UIButton(frame: CGRect(x: 0, y: btn1.bottom+20, width: view.width, height: 60))
        btn2.backgroundColor = UIColor.orange
        btn2.setTitle("隐藏入口", for: .normal)
        btn2.addTarget(self, action: #selector(hiddenEntry), for: .touchUpInside)
        view.addSubview(btn2)
        
    }
    
    @objc func addSubViewAtOtherThread() {
        let globalQueue = DispatchQueue.global()
        globalQueue.async {
            var v  =  UIView()
            self.view.addSubview(v)
        }
    }
    
    @objc func showEntry() {
        DoraemonManager.shareInstance().showDoraemon()
    }
    
    @objc func hiddenEntry() {
        DoraemonManager.shareInstance().hiddenDoraemon()
    }

}
