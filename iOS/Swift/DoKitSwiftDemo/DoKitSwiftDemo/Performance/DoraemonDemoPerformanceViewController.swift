//
//  DoraemonDemoPerformanceViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/15.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoPerformanceViewController: DoraemonDemoBaseViewController {
    var highCpu = false
    var cpuThread: Thread?
    var btn1: UIButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("性能测试Demo")
        
        let btn0 = UIButton(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: 60.0))
        btn0.backgroundColor = UIColor.orange
        btn0.setTitle(DoraemonDemoLocalizedString("低FPS操作打开"), for: .normal)
        btn0.addTarget(self, action: #selector(fpsClick), for: .touchUpInside);
        view.addSubview(btn0)
        
        btn1 = UIButton(frame: CGRect(x: 0, y: btn0.bottom+20, width: view.width, height: 60.0))
        btn1.backgroundColor = UIColor.orange
        btn1.setTitle(DoraemonDemoLocalizedString("高CPU操作打开"), for: .normal)
        btn1.addTarget(self, action: #selector(cpuClick), for: .touchUpInside);
        view.addSubview(btn1)
        
        let btn2 = UIButton(frame: CGRect(x: 0, y: btn1.bottom+20, width: view.width, height: 60.0))
        btn2.backgroundColor = UIColor.orange
        btn2.setTitle(DoraemonDemoLocalizedString("高内存操作打开"), for: .normal)
        btn2.addTarget(self, action: #selector(memoryClick), for: .touchUpInside);
        view.addSubview(btn2)
        
        let btn3 = UIButton(frame: CGRect(x: 0, y: btn2.bottom+20, width: view.width, height: 60.0))
        btn3.backgroundColor = UIColor.orange
        btn3.setTitle(DoraemonDemoLocalizedString("高流量操作打开"), for: .normal)
        btn3.addTarget(self, action: #selector(flowClick), for: .touchUpInside);
        view.addSubview(btn3)
        
        let btn4 = UIButton(frame: CGRect(x: 0, y: btn3.bottom+20, width: view.width, height: 60.0))
        btn4.backgroundColor = UIColor.orange
        btn4.setTitle(DoraemonDemoLocalizedString("卡顿操作打开"), for: .normal)
        btn4.addTarget(self, action: #selector(anrClick), for: .touchUpInside);
        view.addSubview(btn4)
    }
    
    @objc func fpsClick() {
        Thread.sleep(forTimeInterval: 0.5)
    }
    
    @objc func cpuClick() {
        highCpu = !highCpu
        if highCpu {
            cpuThread = Thread(target: self, selector: #selector(highCPUOperate), object: nil)
            cpuThread?.name = "HighCPUThread"
            cpuThread?.start()
            
            btn1.setTitle(DoraemonDemoLocalizedString("高CPU操作关闭"), for: .normal)
        }else{
            cpuThread?.cancel()
            cpuThread = nil
            
            btn1.setTitle(DoraemonDemoLocalizedString("高CPU操作打开"), for: .normal)
        }
    }
    
    @objc func memoryClick() {
        
    }
    
    @objc func flowClick() {
        
    }
    
    @objc func anrClick() {
        
    }
    
    func highCPUOperate() {
        while true {
            if Thread.current {
                <#code#>
            }
        }
    }
}
