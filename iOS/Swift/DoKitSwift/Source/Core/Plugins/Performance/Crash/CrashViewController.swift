//
//  CrashViewController.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/29.
//

import UIKit

class CrashViewController: BaseViewController {

    private lazy var tableView: UITableView = {
        $0.rowHeight = 52
        $0.dataSource = self
        $0.delegate = self
        $0.register(CrashListCell.self, forCellReuseIdentifier: CrashListCell.identifier)
        return $0
    }(UITableView(frame: .zero, style: .plain) )
    
    private static var isCache: Bool {
        get { UserDefaults.standard.bool(forKey: "Dokit.CrashCache.isOn") }
        set { UserDefaults.standard.set(newValue, forKey: "Dokit.CrashCache.isOn")}
    }
    
    private var rows: [Row] = [.switch(isOn: isCache), .log, .clean]
    
    override func viewDidLoad() {
        super.viewDidLoad()
        setup()
    }
    
    private func setup() {
        if #available(iOS 11.0, *) {
            navigationController?.navigationBar.prefersLargeTitles = true
        }
        
        setTitle(title: LocalizedString("Crash"))
        view.addSubview(tableView)
    }
    
    override func needBigTitleView() -> Bool {
        return false
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        tableView.frame = view.bounds
    }
}

// MARK:- UITableViewDataSource & UITableViewDelegate
extension CrashViewController: UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return rows.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: CrashListCell.identifier,
                                                 for: indexPath) as! CrashListCell
        
        let row = rows[indexPath.row]
        switch row {
        case .switch(let isOn):
            cell.delegate = self
            cell.set(.switch(isOn: isOn))
            
        default:                    cell.set(.indicator)
        }
        cell.set(text: row.title)
        return cell
    }
}

extension CrashViewController: UITableViewDelegate {
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        switch rows[indexPath.row] {
        case .log:
            navigationController?.pushViewController(CrashListViewController(), animated: true)
            
        case .clean:
            showAlert(
                title: LocalizedString("提示"),
                message: LocalizedString("确认删除所有崩溃日志吗？"),
                buttonTitles: [LocalizedString("取消"), LocalizedString("确定")]
            ) { index in
                switch index {
                case 1:
                    print("删除")
                    
                default:
                    break
                }
            }
            
        case .switch:
            break
        }
    }
}

extension CrashViewController: CrashListCellDelegate {
    
    func switchAction(cell: CrashListCell, _ isOn: Bool, handle: @escaping (Bool) -> Void) {
        showAlert(
            title: LocalizedString("提示"),
            message: LocalizedString("该功能需要重启App才能生效"),
            buttonTitles: [LocalizedString("取消"), LocalizedString("确定")]
        ) { index in
            switch index {
            case 1:
                CrashViewController.isCache = true
                handle(true)
                exit(0)
                
            default:
                CrashViewController.isCache = false
                handle(false)
            }
        }
    }
}

extension CrashViewController {
    
    enum Row {
        case `switch`(isOn: Bool)
        case log
        case clean
        
        var title: String {
            switch self {
            case .switch:       return LocalizedString("Crash日志收集开关")
            case .log:          return LocalizedString("查看Crash日志")
            case .clean:        return LocalizedString("一键清理Crash日志")
            }
        }
    }
}
