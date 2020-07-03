//
//  MockViewController.swift
//  AFNetworking
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit


enum SwitchState {
    case open, close, all
}

protocol APIGroup {
    var title: String { get }
    var selected: Bool { get }
}

protocol MockViewControllerDataSource {
    var switchStatus: SwitchState { get set }
    var group: [APIGroup] { get }
    
    var apiList: [MockTableViewCellModel] { get }
}

class MockViewController: BaseViewController {

    let dataSource: MockViewControllerDataSource
    
    init(dataSource: MockViewControllerDataSource) {
        self.dataSource = dataSource
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    //MARK: - Override
    override var needBigTitleView: Bool { true }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        view.addSubViews(searchView, groupButton, switchStateButton)
    }
    
    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        searchView.frame = CGRect(x: 16, y: bigTitleView!.frame.maxY + 20, width: kScreenWidth - 32, height: 50)
        groupButton.frame = CGRect(x: 0, y: searchView.frame.maxY, width: view.bounds.size.width / 2, height: 64)
        switchStateButton.frame = CGRect(x: view.bounds.size.width / 2, y: searchView.frame.maxY, width: view.bounds.size.width / 2, height: 64)
    }
    
    //MARK: - Action
    @objc private func groupButtonTouched(sender: UIButton) {
        sender.isSelected = !sender.isSelected
    }
    
    @objc private func switchStateButtonTouched(sender: UIButton) {
        sender.isSelected = !sender.isSelected
    }
    
    //MARK: - View
    lazy var searchView: LogSearchView = {
        $0.delegate = self
        return $0
    }(LogSearchView(frame: .zero))
    
    private lazy var groupButton: MockFilterButton = {
        $0.addTarget(self, action: #selector(groupButtonTouched(sender:)), for: .touchUpInside)
        $0.setTitle(LocalizedString("接口分组"), for: .normal)
        return $0
    }(MockFilterButton())
    
    private lazy var switchStateButton: MockFilterButton = {
        $0.addTarget(self, action: #selector(switchStateButtonTouched(sender:)), for: .touchUpInside)
        $0.setTitle(LocalizedString("开关状态"), for: .normal)
        return $0
    }(MockFilterButton())
    
    lazy var tableView: UITableView = {
        $0.delegate = self
        $0.dataSource = self
        return $0
    }(UITableView(frame: .zero, style: .plain))
    
}

extension MockViewController: UITableViewDelegate, UITableViewDataSource {
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        dataSource.apiList.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        guard let cell = tableView.dequeueReusableCell(withIdentifier: "cell") as? MockTableViewCell else { fatalError() }
        cell.update(dataSource.apiList[indexPath.row])
        return cell
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        dataSource.apiList[indexPath.row].height
    }
    
}

extension MockViewController: LogSearchViewDelegate {
    
    func searchKeyword(keyword: String) {
        
    }
}
