//
//  MockAPIViewController.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

class MockAPIViewController: MockViewController {

    override init(dataSource: MockViewControllerDataSource) {
        super.init(dataSource: dataSource)
        tabBarItem = UITabBarItem(title: LocalizedString("Mock 数据"), image: DKImage(named: "doraemon_mock_data"), selectedImage: DKImage(named: "doraemon_mock_data_selected"))
        
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        bigTitleView?.title = tabBarItem!.title!
        tableView.register(MockAPITableViewCell.self, forCellReuseIdentifier: "cell")
    }

}
