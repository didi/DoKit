//
//  LogViewController.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/10.
//

import UIKit
class LogViewController: BaseViewController, UITableViewDelegate, UITableViewDataSource {
    let switchCellID = "LogSwitchStyleCellID"
    let normalCellID = "LogNormalCellID"

    var logTableView:UITableView?
    var titleArray:[String]?
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: LocalizedString("print"))
        self.initUI()
        self.initData()
    }
    func initUI() {
        logTableView = UITableView.init(frame:CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.frame.size.width, height: self.view.frame.size.height), style: .grouped)
        logTableView?.delegate = self
        logTableView?.dataSource = self
        logTableView?.register(UITableViewCell.self, forCellReuseIdentifier: normalCellID)
        logTableView?.register(LogSwitchStyleCell.self, forCellReuseIdentifier: switchCellID)
        view.addSubview(logTableView!)
    }
    func initData() {
        titleArray = [LocalizedString("开关"),LocalizedString("查看记录")]
    }

    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return titleArray?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let title = titleArray![indexPath.row]
        if indexPath.row == 0 {
            let cell:LogSwitchStyleCell = tableView.dequeueReusableCell(withIdentifier: switchCellID, for: indexPath) as! LogSwitchStyleCell
            cell.selectionStyle = .none
            cell.titleLabel?.text = title;
            let printOn:Bool = LogManager.shared.isOn;
            cell.switchButton?.setOn(printOn, animated: false)
            cell.switchOnBlock = {(isOn:Bool) in
                if isOn {
                    LogManager.shared.start()
                }else {
                    LogManager.shared.stop()
                }
            }
            return cell
        }else {
            let cell:UITableViewCell = tableView.dequeueReusableCell(withIdentifier: normalCellID, for: indexPath)
            cell.textLabel?.text = title
            cell.selectionStyle = .none
            cell.accessoryType = .disclosureIndicator
            return cell
        }
    }
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44
    }
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if indexPath.row == 1 {
//            查看日志
            print("日志收集test")
        }
    }
    
    func hookPrintMethod() {
        
        rebindFunction()
        
    }
    
    
    
    
    
    override func needBigTitleView() -> Bool {
        return true
    }
    
    
}
