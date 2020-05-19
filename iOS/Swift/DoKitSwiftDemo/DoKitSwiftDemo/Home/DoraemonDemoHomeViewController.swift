//
//  DoraemonDemoHomeViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoHomeViewController: DoraemonDemoBaseViewController, UITableViewDelegate, UITableViewDataSource {
    var tableView: UITableView!
    var items: [String]! = [] {
        didSet {
            self.tableView.reloadData()
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("DoraemonKit")
        
        tableView = UITableView(frame: view.bounds)
        tableView.delegate = self
        tableView.dataSource = self
        view.addSubview(tableView)
        
        items  = ["沙盒测试Demo",
                 "日志测试Demo",
                 "性能测试Demo",
                 "视觉测试Demo",
                 "网络测试Demo",
                 "模拟位置Demo",
                 "crash触发Demo",
                 "通用测试Demo",
                 "内存泄漏测试",
                 "Call OC"];
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: "HomeCellId")
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: "HomeCellId")
        }
        cell?.textLabel?.text = DoraemonDemoLocalizedString(items[indexPath.row])
        
        return cell!;
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let row = indexPath.row
        var vc : UIViewController
        switch row {
        case 0:
            vc = DoraemonDemoSanboxViewController()
        case 1:
            vc = DoraemonDemoLoggerViewController()
        case 2:
            vc = DoraemonDemoPerformanceViewController()
        case 3:
            vc = DoraemonDemoUIViewController()
        case 4:
            vc = DoraemonDemoNetViewController()
        case 5:
            vc = DoraemonDemoGPSViewController()
        case 6:
            vc = DoraemonDemoCrashViewController()
        case 7:
            vc = DoraemonDemoCommonViewController()
        case 8:
            vc = DoraemonDemoMemoryLeakViewController()
        default:
            vc = DoraemonDemoOCViewController()
        }
        
        navigationController?.pushViewController(vc, animated: true)
    }

}
