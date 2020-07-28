//
//  UserDefaultsViewController.swift
//  DoraemonKit-Swift
//
//  Created by Smallfly on 2020/7/27.
//

import UIKit

struct UserDefaultsModel {
    let key: String
    let value: String
}

let kUITableViewCell = "kUITableViewCell"

class UserDefaultsViewController: BaseViewController {

    var modelList: [UserDefaultsModel] = []
    
    override func viewDidLoad() {
        super.viewDidLoad()
        title = "UserDefaults"
        setupNavItem()
        
        view.addSubview(tableView)
    }

    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        reloadData()
    }
    
    func setupNavItem() {
        let item = UIBarButtonItem(barButtonSystemItem: .trash, target: self, action: #selector(clearData))
        navigationItem.rightBarButtonItem = item
    }
    
    func reloadData() {
        let dic = UserDefaults.standard.dictionaryRepresentation()
        modelList = dic.map { UserDefaultsModel(key: $0, value: stringify($1)) }
        modelList.sort { $0.key < $1.key }
        tableView.reloadData()
    }
    
    func stringify(_ anyValue: Any?) -> String {
        var content  = anyValue.debugDescription
        content = content.replacingOccurrences(of: "Optional(", with: "")
        content.removeLast()
        return content
    }

    @objc func clearData() {
        AlertUtil.handleAlertAction(vc: self, title: LocalizedString("提示"), text: "clear all data?", ok: LocalizedString("确定"), cancel: LocalizedString("取消"), okBlock: {
            if let domainName = Bundle.main.bundleIdentifier {
                UserDefaults.standard.removePersistentDomain(forName: domainName)
            }
            self.modelList.removeAll()
            self.reloadData()
        }) {}
    }
    
    // MARK: -
    
    lazy var tableView: UITableView = { [unowned self] in
        let tableView = UITableView(frame: UIScreen.main.bounds, style: .plain)
        tableView.backgroundColor = .white
        tableView.dataSource = self
        tableView.delegate = self
        return tableView
    } ()
}

extension UserDefaultsViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return modelList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell: UITableViewCell
        if let cachedCell = tableView.dequeueReusableCell(withIdentifier: kUITableViewCell) {
            cell = cachedCell
        } else {
            cell = UITableViewCell(style: .subtitle, reuseIdentifier: kUITableViewCell)
        }
        cell.textLabel?.text = modelList[indexPath.row].key
        cell.detailTextLabel?.text = modelList[indexPath.row].value
        return cell
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        if (editingStyle == .delete) {
            UserDefaults.standard.set(nil, forKey: modelList[indexPath.row].key)
            modelList.remove(at: indexPath.row)
            tableView.deleteRows(at: [indexPath], with: .automatic)
        }
    }
    
    func tableView(_ tableView: UITableView, editingStyleForRowAt indexPath: IndexPath) -> UITableViewCell.EditingStyle {
        return .delete
    }

    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let editViewController = UserDefaultsEditViewController(with: modelList[indexPath.row])
        navigationController?.pushViewController(editViewController, animated: true)
        
        tableView.deselectRow(at: indexPath, animated: true)
    }
}
