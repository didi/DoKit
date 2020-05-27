//
//  DoKitAppInfoViewController.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

class DoKitAppInfoViewController: DoKitBaseViewController {
    var infoTableView:UITableView?
    var datas:[Dictionary<String, [[String]]>]?
    override func viewDidLoad() {
        self.setTitle(title: DoKitLocalizedString("App信息"))
        
        view.backgroundColor = UIColor.purple
                    
        infoTableView = UITableView.init(frame: view.bounds, style: .grouped)
        infoTableView?.delegate = self;
        infoTableView?.dataSource = self;
        view.addSubview(infoTableView!)
        infoTableView?.register(DoKitAppInfoCell.self, forCellReuseIdentifier: "AppInfoCell")
        
        let infoArr = [["设备名称",UIDevice.current.name],
                   ["手机型号",UIDevice.current.localizedModel]]
        datas = [["手机信息":infoArr]]
    }
}

extension DoKitAppInfoViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return datas!.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let rows = datas?[section].values.first
        return rows!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "AppInfoCell", for: indexPath) as! DoKitAppInfoCell
        let group = datas?[indexPath.section]
        let item = group?.values.first![indexPath.row]
        cell.titleLabel?.text = item?.first
        cell.contentLabel?.text = item?.last
        return cell
    }
}

extension DoKitAppInfoViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 38
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView.init()
        let titleLabel = UILabel.init()
        titleLabel.text = datas?[section].keys.first
        titleLabel.font = .systemFont(ofSize: 15)
        headerView.addSubview(titleLabel)
        titleLabel.snp.makeConstraints { (make) in
            make.left.equalTo(headerView).offset(16)
            make.centerY.equalTo(headerView)
        }
        return headerView
    }
}
