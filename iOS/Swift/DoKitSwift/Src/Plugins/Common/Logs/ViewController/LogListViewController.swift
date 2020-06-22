//
//  LogListViewController.swift
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/19.
//

import UIKit

class LogListViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource,LogSearchViewDelegate {
    let logListCellID = "logListCellID"

    var logListTableView:UITableView?
    var dataArray:[LogModel]?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.initUI()
        self.loadData()

    }
    private func initUI() {
        title = LocalizedString("print日志记录")
        //导航item
        let exportItem = UIBarButtonItem.init(title: LocalizedString("导出"), style: .plain, target: self, action: #selector(exportLog))
        let clearItem = UIBarButtonItem.init(title: LocalizedString("清除"), style: .plain, target: self, action: #selector(clearLog))
        navigationItem.setRightBarButtonItems([exportItem,clearItem], animated: false)
        
        
        //搜索框
        let searchView:LogSearchView = LogSearchView.init(frame: CGRect.init(x: 16, y: kIphoneNavBarHeight + kIphoneStatusBarHeight, width: kScreenWidth - 32, height: 50))
        searchView.delegate = self
        view.addSubview(searchView)
        
        //列表
        logListTableView = UITableView.init(frame:.zero, style: .grouped)
        logListTableView?.delegate = self
        logListTableView?.dataSource = self
        logListTableView?.sectionHeaderHeight = 0.1
        logListTableView?.separatorStyle = .none
        logListTableView?.backgroundColor = .white
        logListTableView?.register(LogListCell.self, forCellReuseIdentifier: logListCellID)
        view.addSubview(logListTableView!)
        logListTableView?.translatesAutoresizingMaskIntoConstraints = false
        view.addConstraint(NSLayoutConstraint.init(item: logListTableView!, attribute: .top, relatedBy: .equal, toItem: searchView, attribute: .bottom, multiplier: 1.0, constant: 0))
        view.addConstraint(NSLayoutConstraint.init(item: logListTableView!, attribute: .leading, relatedBy: .equal, toItem: view, attribute: .leading, multiplier: 1.0, constant: 0))
        view.addConstraint(NSLayoutConstraint.init(item: logListTableView!, attribute: .trailing, relatedBy: .equal, toItem: view, attribute: .trailing, multiplier: 1.0, constant: 0))
        view.addConstraint(NSLayoutConstraint.init(item: logListTableView!, attribute: .bottom, relatedBy: .equal, toItem: view, attribute: .bottom, multiplier: 1.0, constant: 0))
    }
    func loadData() {
        dataArray = LogManager.shared.logs.reversed()
    }
    
    @objc func exportLog() {
        let logArray = LogManager.shared.logs
        let logString:NSMutableString = .init()
        for log in logArray {
            logString.append(log.dateFormat)
            logString.append(" ")
            logString.append(log.content ?? "")
            logString.append("\n")
        }
        DoKitUtil.share(with: logString, self)

    }
    
    @objc func clearLog() {
        LogManager.shared.clearLog()
        loadData()
        logListTableView?.reloadData()
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataArray?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let log = (dataArray?[indexPath.row])!
        let cell:LogListCell = tableView.dequeueReusableCell(withIdentifier: logListCellID, for: indexPath) as! LogListCell
        cell.renderWithModel(model: log)
        return cell
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        let log = (self.dataArray?[indexPath.row])!
        if log.expand {
            return log.cellHeight!
        }else {
            return 50;
        }
    }
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 0.1
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let log = (self.dataArray?[indexPath.row])!
        log.expand = !log.expand
        tableView.reloadData()
    }
    
    func searchKeyword(keyword: String) {
        if keyword.count > 0 {
            loadData()
            var matchArray = [LogModel]()
            for log:LogModel in dataArray! {
                if (log.content?.contains(keyword))! {
                    matchArray.append(log)
                }
            }
            dataArray = matchArray
            
        }else {
            loadData()
        }
        logListTableView?.reloadData()
    }
}
