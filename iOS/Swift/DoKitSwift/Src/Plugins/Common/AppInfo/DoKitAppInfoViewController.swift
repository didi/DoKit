//
//  DoKitAppInfoViewController.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

let IOS_CELLULAR = "pdp_ip0"
let IOS_WIFI     = "en0"
let IP_ADDR_IPv4 = "ipv4"
let IP_ADDR_IPv6 = "ipv6"

class DoKitAppInfoViewController: BaseViewController {
    var infoTableView:UITableView?
    var datas:[Dictionary<String, [[String]]>]?
    override func viewDidLoad() {
        self.setTitle(title: LocalizedString("App信息"))
        
        view.backgroundColor = UIColor.purple
                    
        infoTableView = UITableView.init(frame: view.bounds, style: .grouped)
        infoTableView?.delegate = self;
        infoTableView?.dataSource = self;
        view.addSubview(infoTableView!)
        infoTableView?.register(DoKitAppInfoCell.self, forCellReuseIdentifier: "AppInfoCell")
        
        let deviceArr = [[LocalizedString("设备名称"),UIDevice.current.name],
                   [LocalizedString("手机型号"),UIDevice.current.localizedModel],
                   [LocalizedString("系统版本"),UIDevice.current.systemVersion],
                   [LocalizedString("手机屏幕"),"\(String(format: "%.0f", UIScreen.main.bounds.size.width)) * \(String(format: "%.0f", UIScreen.main.bounds.size.height))"],
                   //[DoKitLocalizedString("ipV4"),UIDevice.current.localizedModel],
//                   [DoKitLocalizedString("ipV6"),UIDevice.current.localizedModel]
        ]
        datas = [[LocalizedString("手机信息"):deviceArr]]
        let infoDict = Bundle.main.infoDictionary
        
        var infoArr = [[LocalizedString("Bundle ID"), Bundle.main.bundleIdentifier!],
                       [LocalizedString("Build"), String.safeString(obj: infoDict?["CFBundleVersion"])],
                       [LocalizedString("VersionCode"), String.safeString(obj: infoDict?["CFBundleShortVersionString"])]]
        if (DoKit.shared.customAppInfo != nil) {
            for item in DoKit.shared.customAppInfo!() {
                infoArr.append(item)
            }
        }
        datas?.append([LocalizedString("App信息"):infoArr])
        
        let authorityArr = [[LocalizedString("地理位置权限"), DoKitAuthorityUtil.locationAuthority()],
                            [LocalizedString("网络权限"), "Unknown"],
                            [LocalizedString("推送权限"), DoKitAuthorityUtil.pushAuthority()],
                            [LocalizedString("麦克风权限"), DoKitAuthorityUtil.audioAuthority()],
                            [LocalizedString("相册权限"), DoKitAuthorityUtil.photoAuthority()],
                            [LocalizedString("相机权限"), DoKitAuthorityUtil.cameraAuthority()],
                            [LocalizedString("通讯录权限"), DoKitAuthorityUtil.addressAuthority()],
                            [LocalizedString("日历权限"), DoKitAuthorityUtil.calendarAuthority()],
                            [LocalizedString("提醒事项权限"), DoKitAuthorityUtil.remindAuthority()],

        ]
        
        datas?.append([LocalizedString("权限信息"): authorityArr])
    }
}

extension DoKitAppInfoViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return datas!.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let rows = datas?[section].values.first
        return rows!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "AppInfoCell", for: indexPath) as! DoKitAppInfoCell
        let group = datas?[indexPath.section]
        let item = group?.values.first![indexPath.row]
        cell.titleLabel?.text = item?.first
        let value = item?.last
        let kv = ["NotDetermined":LocalizedString("用户没有选择"),
                  "Restricted":LocalizedString("家长控制"),
                  "Denied":LocalizedString("用户没有授权"),
                  "Authorized":LocalizedString("用户已经授权")]
        cell.contentLabel?.text = kv[value ?? ""] ?? value
        return cell
    }
}

extension DoKitAppInfoViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 38
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView.init()
        let titleLabel = UILabel.init()
        titleLabel.text = datas?[section].keys.first
        titleLabel.font = .boldSystemFont(ofSize: 16)
        headerView.addSubview(titleLabel)
        titleLabel.snp.makeConstraints { (make) in
            make.left.equalTo(headerView).offset(16)
            make.centerY.equalTo(headerView)
        }
        return headerView
    }
}

extension String {
    static func safeString(obj:Any?) -> String {
        if obj is String {
            return obj as! String
        }
        return ""
    }
}
