//
//  MockUploadViewController.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/30.
//

import UIKit

class MockUploadViewController: MockViewController {

    override init(dataSource: MockViewControllerDataSource) {
        super.init(dataSource: dataSource)
        tabBarItem = UITabBarItem(title: LocalizedString("上传模板"), image: DKImage(named: "doraemon_mock_up"), selectedImage: DKImage(named: "doraemon_mock_up_selected"))
        
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
