//
//  HealthInstructionsView.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthInstructionsView: UIView {
    
    private lazy var tableView: UITableView = {
        $0.delegate = self
        $0.dataSource = self
        $0.separatorStyle = UITableViewCell.SeparatorStyle.none
        $0.sizeToFit()
        $0.register(HealthInstructionsCell.self, forCellReuseIdentifier: HealthInstructionsCell.identifier)
        return $0
    }(UITableView())
    
    var itemTitleArray = [String]()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        
        itemTitleArray = [
            "点击开始体检按钮开始本次的性能数据手机。",
            "在每一个页面至少停留10秒钟，如果低于10秒钟的话，我们将会丢弃该页面的收集数据。",
            "测试完毕之后，重新进入该页面，点击结束测试按钮，填写本次的测试用例名称和测试人的名字，即可上传。",
            "打开dokit.cn平台，进入app健康体检列表，查看本次的数据报告。"
        ];

        self.addSubview(tableView)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        tableView.frame = CGRect(x: 0, y: kSizeFrom750_Landscape(89), width: self.width, height: self.height)
    }
}

// MARK: - UITableViewDelegate, UITableViewDataSource
extension HealthInstructionsView: UITableViewDelegate, UITableViewDataSource {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return itemTitleArray.count
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 80
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: HealthInstructionsCell.identifier) as! HealthInstructionsCell
        let title = LocalizedString("第\(indexPath.row + 1)步")
        cell.renderUI(title: title, itemLabel: itemTitleArray[indexPath.row])
        return cell
    }
}
