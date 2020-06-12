//
//  LogViewController.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/10.
//

import UIKit

class LogViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource {
    let switchCellID = "LogSwitchStyleCell"
    
    var logTableView:UITableView?
    var titleArray:[String]?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: LocalizedString("NSLog"))
        self.initUI()
        self.initData()
    }
    func initUI() {
        logTableView = UITableView.init(frame: view.bounds, style: .grouped)
        logTableView?.delegate = self
        logTableView?.dataSource = self
        view.addSubview(logTableView!)
        logTableView?.register(LogSwitchStyleCell.self, forCellReuseIdentifier: switchCellID)
    }
    func initData() {
        titleArray = [LocalizedString("开关"),LocalizedString("查看记录")]
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return titleArray?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: switchCellID, for: indexPath)
        return cell
    }
    
}
