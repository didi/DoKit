//
//  ANRTool.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

struct ANRTool {
    
    /// 保存卡顿记录到沙盒中的Library/Caches/ANR目录下
    /// - Parameter info: 卡顿信息的内容
    static func saveANRInfo(info: [String: Any]) {
        guard info.count > 0, let title = info["title"] as? String else { return }
        let manager = FileManager.default
        if anrDirectory.count > 0 && manager.fileExists(atPath: anrDirectory) {
            let anrPath = (anrDirectory as NSString).appendingPathComponent("ANR \(title).plist")
            (info as NSDictionary).write(toFile: anrPath, atomically: true)
        }
    }
    
    static var anrDirectory: String {
        guard let cachePath = NSSearchPathForDirectoriesInDomains(.cachesDirectory, .userDomainMask, true).first else {
            return ""
        }
        let directory = (cachePath as NSString).appendingPathComponent("ANR")
        let manager = FileManager.default
        if !manager.fileExists(atPath: directory) {
            try? manager.createDirectory(atPath: directory, withIntermediateDirectories: true, attributes: nil)
        }
        return directory
    }
}
