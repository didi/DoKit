//
//  LogListViewController.swift
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/19.
//

import UIKit

class LogListViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource {
    
    let logListCellID = "logListCellID"

    var logListTableView:UITableView?
    var dataArray:[LogModel]?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initUI()
        self.loadData()

    }
    private func initUI() {
        //导航item
        let exportItem = UIBarButtonItem.init(title: LocalizedString("导出"), style: .plain, target: self, action: #selector(exportLog))
        let clearItem = UIBarButtonItem.init(title: LocalizedString("清除"), style: .plain, target: self, action: #selector(clearLog))
        self.navigationItem.setRightBarButtonItems([exportItem,clearItem], animated: false)
        
        
        //搜索框
        
        
        //列表
        logListTableView = UITableView.init(frame:CGRect(x: 0, y: kIphoneNavBarHeight, width: self.view.frame.size.width, height: self.view.frame.size.height), style: .grouped)
        logListTableView?.delegate = self
        logListTableView?.dataSource = self
        logListTableView?.register(UITableViewCell.self, forCellReuseIdentifier: logListCellID)
        view.addSubview(logListTableView!)
        
        
    }
    func loadData() {
        self.dataArray = LogManager.shared.logs.reversed()
    }
    
    
    @objc func exportLog() {
        print("exportLog")
    }
    
    @objc func clearLog() {
        LogManager.shared.clearLog()
        print("clearLog")
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.dataArray?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let log = (self.dataArray?[indexPath.row])!
        
        let cell:LogListCell = tableView.dequeueReusableCell(withIdentifier: logListCellID, for: indexPath) as! LogListCell
        cell.renderWithModel(model: log)
        
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let log = (self.dataArray?[indexPath.row])!
        return log.cellHeight ?? 0
    }
}
