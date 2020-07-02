//
//  MockPlugin.swift
//  AFNetworking
//
//  Created by 郝振壹 on 2020/6/29.
//

import Foundation

struct MockPlugin: Plugin {
    
    var module: PluginModule { .platform }
    
    var title: String { LocalizedString("Mock 数据") }
    
    var icon: UIImage? { DKImage(named: "doraemon_mock") }
    
    func onInstall() {
        
    }
    
    func onSelected() {
        guard DoKit.shared.pid != nil else {
            ToastUtil.showToast(LocalizedString("需要到www.dokit.cn上注册pId才能使用该功能"), superView: HomeWindow.shared.rootViewController?.view)
            return
        }
        
        let dataSource = MockAPIDataSource()
        dataSource.fetchRemoteData {
            DispatchQueue.main.async {
                HomeWindow.shared.openPlugin(vc: MockViewController(dataSource: dataSource))
            }
        }
    }
    
}
