//
//  AppInfoViewController.swift
//  DoraemonKit
//
//  Created by Rake Yang on 2020/5/27.
//

import Foundation

let IOS_CELLULAR = "pdp_ip0"
let IOS_WIFI     = "en0"
let IP_ADDR_IPv4 = "ipv4"
let IP_ADDR_IPv6 = "ipv6"

class AppInfoViewController: BaseViewController {
    var infoTableView:UITableView?
    var datas:[Dictionary<String, [[String]]>]?
    override func viewDidLoad() {
        self.setTitle(title: LocalizedString("App信息"))
        
        view.backgroundColor = UIColor.purple
                    
        infoTableView = UITableView.init(frame: view.bounds, style: .grouped)
        infoTableView?.delegate = self;
        infoTableView?.dataSource = self;
        view.addSubview(infoTableView!)
        infoTableView?.register(AppInfoCell.self, forCellReuseIdentifier: "AppInfoCell")
        
        let deviceArr = [[LocalizedString("设备名称"),UIDevice.current.name],
                   [LocalizedString("手机型号"),UIDevice.current.localizedModel],
                   [LocalizedString("系统版本"),UIDevice.current.systemVersion],
                   [LocalizedString("手机屏幕"),"\(String(format: "%.0f", kScreenWidth)) * \(String(format: "%.0f", kScreenHeight))"],
                   [LocalizedString("IP"), String.safeString(obj: ipv4())],
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
        
        let authorityArr = [[LocalizedString("地理位置权限"), AuthorityUtil.locationAuthority()],
                            [LocalizedString("网络权限"), "Unknown"],
                            [LocalizedString("推送权限"), AuthorityUtil.pushAuthority()],
                            [LocalizedString("麦克风权限"), AuthorityUtil.audioAuthority()],
                            [LocalizedString("相册权限"), AuthorityUtil.photoAuthority()],
                            [LocalizedString("相机权限"), AuthorityUtil.cameraAuthority()],
                            [LocalizedString("通讯录权限"), AuthorityUtil.addressAuthority()],
                            [LocalizedString("日历权限"), AuthorityUtil.calendarAuthority()],
                            [LocalizedString("提醒事项权限"), AuthorityUtil.remindAuthority()],

        ]
        
        datas?.append([LocalizedString("权限信息"): authorityArr])
    }
}

extension AppInfoViewController: UITableViewDataSource {
    func numberOfSections(in tableView: UITableView) -> Int {
        return datas!.count
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        let rows = datas?[section].values.first
        return rows!.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "AppInfoCell", for: indexPath) as! AppInfoCell
        cell.translatesAutoresizingMaskIntoConstraints = false
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

extension AppInfoViewController: UITableViewDelegate {
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 44
    }
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 38
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerView = UIView.init()
        let titleLabel = UILabel.init()
        titleLabel.text = datas?[section].keys.first
        titleLabel.font = .boldSystemFont(ofSize: 16)
        titleLabel.translatesAutoresizingMaskIntoConstraints = false;
        headerView.addSubview(titleLabel)
        headerView.addConstraint(NSLayoutConstraint.init(item: titleLabel, attribute: .leading, relatedBy: .equal, toItem: headerView, attribute: .leading, multiplier: 1, constant: 16))
        headerView.addConstraint(NSLayoutConstraint.init(item: titleLabel, attribute: .centerY, relatedBy: .equal, toItem: headerView, attribute: .centerY, multiplier: 1, constant: 0))
        return headerView
    }
}

extension AppInfoViewController {
    func ipv4() -> String? {
        var addresses = [String]()
        var ifaddr : UnsafeMutablePointer<ifaddrs>? = nil
        if getifaddrs(&ifaddr) == 0 {
            var ptr = ifaddr
            while (ptr != nil) {
                let flags = Int32(ptr!.pointee.ifa_flags)
                var addr = ptr!.pointee.ifa_addr.pointee
                if (flags & (IFF_UP|IFF_RUNNING|IFF_LOOPBACK)) == (IFF_UP|IFF_RUNNING) {
                    if addr.sa_family == UInt8(AF_INET) || addr.sa_family == UInt8(AF_INET6) {
                        var hostname = [CChar](repeating: 0, count: Int(NI_MAXHOST))
                        if (getnameinfo(&addr, socklen_t(addr.sa_len), &hostname, socklen_t(hostname.count),nil, socklen_t(0), NI_NUMERICHOST) == 0) {
                            if let address = String(validatingUTF8:hostname) {
                                if String.init(cString: ptr!.pointee.ifa_name).hasPrefix("en") {
                                    addresses.append(address)
                                }
                            }
                        }
                    }
                }
                ptr = ptr!.pointee.ifa_next
            }
            freeifaddrs(ifaddr)
        }
        
        return addresses.joined(separator: "，")
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
