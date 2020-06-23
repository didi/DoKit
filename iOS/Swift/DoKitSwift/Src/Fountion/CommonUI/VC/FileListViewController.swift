//
//  FileListViewController.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/6/23.
//

import Foundation

fileprivate struct FileModel {
    let isDirectory: Bool
    let path: String
    let name: String
    let size: UInt64
    let createDate: Date
}

class FileListViewController: BaseViewController {
    
    let directory: String
    init(directory: String) {
        self.directory = directory
        super.init(nibName: nil, bundle: nil)
        setTitle(title: LocalizedString((directory as NSString).lastPathComponent))
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var tableView: UITableView = {
        $0.rowHeight = kSizeFrom750_Landscape(104)
        $0.delegate = self
        $0.dataSource = self
        $0.register(FileCell.self, forCellReuseIdentifier: self.cellId)
        return $0
    }( UITableView(frame: .zero, style: .plain) )
    
    fileprivate var dataSource = [FileModel]()
    private let cellId = "FileCell"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        tableView.frame = CGRect(x: 0, y: kIphoneNavBarHeight, width: kScreenWidth, height: kScreenHeight - kIphoneNavBarHeight)
        view.addSubview(tableView)
        tableView.tableFooterView = UIView()
        loadFiles()
    }
    
    fileprivate func loadFiles(){
        let directory = self.directory
        let manager = FileManager.default
        var isDirectory: ObjCBool = false
        let isExists = manager.fileExists(atPath: directory, isDirectory: &isDirectory)
        if !isExists || !isDirectory.boolValue {return}
        guard let paths = try? manager.contentsOfDirectory(atPath: directory) else{
            return
        }
        let files = paths.map { (p) -> FileModel in
            let path = (directory as NSString).appendingPathComponent(p)
            let info = try? manager.attributesOfItem(atPath: path)
            var isDirectory = false
            if let type = info?[FileAttributeKey.type] as? FileAttributeType,type == .typeDirectory{
                isDirectory = true
            }
            let date = (info?[FileAttributeKey.creationDate] as? Date) ?? Date()
            let size = (info?[FileAttributeKey.size] as? UInt64) ?? 0
            return FileModel.init(isDirectory: isDirectory, path: path, name: p, size: size, createDate: date)
        }.sorted { (m1, m2) -> Bool in
            return m1.createDate.timeIntervalSince(m2.createDate) > 0
        }
        self.dataSource = files
        tableView.reloadData()
    }
}


extension FileListViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return dataSource.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: cellId, for: indexPath) as! FileCell
        cell.renderCell(model: dataSource[indexPath.row])
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
            try? FileManager.default.removeItem(atPath: dataSource[indexPath.row].path)
            loadFiles()
        }
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        tableView.deselectRow(at: indexPath, animated: true)
        if indexPath.row < dataSource.count {
            let file = dataSource[indexPath.row]
            if file.isDirectory {
                let vc = FileListViewController.init(directory: file.path)
                navigationController?.pushViewController(vc, animated: true)
            }else{
                let vc = FilePreviewController.init(filePath: file.path)
                navigationController?.pushViewController(vc, animated: true)
            }
        }
    }
}


class FileCell: UITableViewCell {
    
    lazy var titleLabel: UILabel = {
        $0.textColor = .black_1()
        $0.font = .systemFont(ofSize: kSizeFrom750_Landscape(32))
        return $0
    }(UILabel())
    lazy var arrowImageView = UIImageView(image: DKImage(named: "doraemon_more"))
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        selectionStyle = .none

        [titleLabel, arrowImageView].forEach { contentView.addSubview($0) }
        
        titleLabel.translatesAutoresizingMaskIntoConstraints = false
        titleLabel.centerYAnchor.constraint(equalTo: contentView.centerYAnchor).isActive = true
        titleLabel.leftAnchor.constraint(equalTo: contentView.leftAnchor, constant: kSizeFrom750_Landscape(32)).isActive = true
        
        arrowImageView.translatesAutoresizingMaskIntoConstraints = false
        arrowImageView.centerYAnchor.constraint(equalTo: contentView.centerYAnchor).isActive = true
        arrowImageView.rightAnchor.constraint(equalTo: contentView.rightAnchor, constant: -kSizeFrom750_Landscape(32)).isActive = true
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    fileprivate func renderCell(model: FileModel) {
        titleLabel.text = model.name
    }

}
