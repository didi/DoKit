//
//  MockAPIDatasource.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import Foundation

struct MockGroup: APIGroup {
    var title: String
    var selected = false
    
    static var all = MockGroup(title: LocalizedString("全部"), selected: true)
}


class MockAPIDataSource: MockViewControllerDataSource {
    
    var switchStatus: SwitchState = .all
    
    var group: [APIGroup] = [MockGroup.all]
    
    var apiList: [MockTableViewCellModel] = []
    
    func fetchRemoteData(_ completion: @escaping ()->()) {
        guard let pid = DoKit.shared.pid else { return }
        let param = ["projectId":pid,
                     "isfull":"1",
                     "curPage":"1",
                     "pageSize":"1000"]
        
        Network.get(url: "https://mock.dokit.cn/api/app/interface", param: param) { (error, dict) in
            if let error = error {
                print(error)
                return
            }
            if let dict = dict {
                print(dict)
                completion()
            }
        }
    }
}
