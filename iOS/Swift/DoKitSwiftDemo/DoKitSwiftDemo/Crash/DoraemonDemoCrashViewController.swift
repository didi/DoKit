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
        let array = ["A","B","C"]
        var v = array[5]
    }
    
    @objc func signalExceptionBtnClicked(_ button: UIButton) {

    }

}
