//
//  DoraemonDemoCrashViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/19.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoCrashViewController: DoraemonDemoBaseViewController {
    
    var uncaughtExceptionBtn: UIButton!
    var signalExceptionBtn: UIButton!

    override func viewDidLoad() {
        super.viewDidLoad()
         self.title = "Crash";
        
        uncaughtExceptionBtn = UIButton(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: 60))
        uncaughtExceptionBtn.backgroundColor = UIColor.orange
        uncaughtExceptionBtn.setTitle("uncaughtException", for: .normal)
        uncaughtExceptionBtn.addTarget(self, action: #selector(uncaughtExceptionBtnClicked(button:)), for: .touchUpInside)
        view.addSubview(uncaughtExceptionBtn)
        
        signalExceptionBtn = UIButton(frame: CGRect(x: 0, y: uncaughtExceptionBtn.bottom+20, width: view.width, height: 60))
        signalExceptionBtn.backgroundColor = UIColor.orange
        signalExceptionBtn.setTitle("signalException", for: .normal)
        signalExceptionBtn.addTarget(self, action: #selector(signalExceptionBtnClicked(_:)), for: .touchUpInside)
        view.addSubview(signalExceptionBtn)
    }
    
    @objc func uncaughtExceptionBtnClicked(button: UIButton) {
        let arr = NSArray()
        arr[10]
    }
    
    @objc func signalExceptionBtnClicked(_ button: UIButton) {
        //SIGBUS，内存地址未对齐
        //Thread 1: EXC_BAD_INSTRUCTION (code=EXC_I386_INVOP, subcode=0x0)
        var string: String? = "1"
        string = nil
        _ = string!.lowercased()
    }

}
