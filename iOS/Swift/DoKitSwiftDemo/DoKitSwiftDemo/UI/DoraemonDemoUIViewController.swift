//
//  DoraemonDemoUIViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/18.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoUIViewController: DoraemonDemoBaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        
        let redView: UIView! = UIView(frame: CGRect(x: 100, y: 200, width: 60, height: 60))
        redView.backgroundColor = UIColor.red
        view.addSubview(redView)
        
        let titleLabel: UILabel! = UILabel(frame: CGRect(x: 100, y: 400, width: 200, height: 60))
        titleLabel.text = DoraemonDemoLocalizedString("我是来测试的")
        titleLabel.backgroundColor = UIColor.hexColor(0x00FF00)
        titleLabel.textColor = UIColor.hexColor(0xFF0000)
        view.addSubview(titleLabel)
        
        let input: UITextField! = UITextField(frame: CGRect(x: 100, y: 300, width: 200, height: 50))
        input.textAlignment = .center
        input.keyboardType = .numberPad
        input.backgroundColor = UIColor.lightGray
        view.addSubview(input)
        
        let input2: UITextField! = UITextField(frame: CGRect(x: 100, y: 500, width: 200, height: 50))
        input2.textAlignment = .center
        input2.backgroundColor = UIColor.lightGray
        view.addSubview(input2)
        
        let btn: UIButton! = UIButton(frame: CGRect(x: 200, y: 200, width: 200, height: 50))
        btn.backgroundColor = UIColor.lightGray
        btn.layer.cornerRadius = 8
        btn.setTitle("UIMenuController", for: .normal)
        btn.addTarget(self, action: #selector(deleteBtnAction), for: .touchUpInside)
        view.addSubview(btn)
    }
    
    @objc func deleteBtnAction() {
        print("deleteBtnAction")
    }
    
}
