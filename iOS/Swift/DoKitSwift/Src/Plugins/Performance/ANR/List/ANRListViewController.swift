//
//  ANRListViewController.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

class ANRListViewController: BaseViewController {
    
    lazy var tableView: UITableView = {
        $0.rowHeight = kSizeFrom750_Landscape(104)
        $0.delegate = self
        $0.dataSource = self
        $0.register(ANRListCell.self, forCellReuseIdentifier: self.cellId)
        return $0
    }( UITableView(frame: .zero, style: .plain) )
    
    var dataSource = [String]()
    private let cellId = "ANRCell"
    
    override func viewDidLoad() {
        super.viewDidLoad()

        setTitle(title: LocalizedString("卡顿列表"))
        tableView.frame = CGRect(x: 0, y: kIphoneNavBarHeight, width: kScreenWidth, height: kScreenHeight - kIphoneNavBarHeight)
        view.addSubview(tableView)
        tableView.tableFooterView = UIView()
        loadANRData()
    }
    
    func loadANRData() {
        let anrDir = ANRTool.anrDirectory
        let manager = FileManager.default
        guard anrDir.count > 0 && manager.fileExists(atPath: anrDir) else { return }
        guard var paths = try? manager.contentsOfDirectory(atPath: anrDir) else { return }
        paths.sort { (p1, p2) -> Bool in
            let path1 = (anrDir as NSString).appendingPathComponent(p1)
            let path2 = (anrDir as NSString).appendingPathComponent(p2)
            let info1 = try? manager.attributesOfItem(atPath: path1)
            let info2 = try? manager.attributesOfItem(atPath: path2)
            
            if let v1 = info1?[FileAttributeKey.creationDate] as? Date, let v2 = info2?[FileAttributeKey.creationDate] as? Date {
                return v1.timeIntervalSince(v2) > 0
            }
            return true
        }
        dataSource = paths
        tableView.reloadData()
    }
}

extension ANRListViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! ANRListCell
        cell.renderCell(title: dataSource[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return kSizeFrom750_Landscape(104)
    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    func tableView(_ tableView: UITableView, titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return LocalizedString("删除")
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if indexPath.row < dataSource.count {
            let fullPath = (ANRTool.anrDirectory as NSString).appendingPathComponent(dataSource[indexPath.row])
            try? FileManager.default.removeItem(atPath: fullPath)
            loadANRData()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.row < dataSource.count {
            let path = dataSource[indexPath.row]
            let fullPath = (ANRTool.anrDirectory as NSString).appendingPathComponent(path)
            let detailVc = ANRDetailViewController()
            detailVc.filePath = fullPath
            navigationController?.pushViewController(detailVc, animated: true)
        }
    }
}
