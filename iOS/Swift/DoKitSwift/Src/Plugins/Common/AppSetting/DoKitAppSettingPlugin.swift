//
//  DoKitAppSettingPlugin.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import Foundation


class DoKitAppSettingPlugin: DoKitBasePlugin{
    override func pluginDidLoad() {
        DoKitUtil.openAppSetting()
        DoKitHomeWindow.shared.hide()
    }
}
