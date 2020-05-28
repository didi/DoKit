//
//  DoKitUtil.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import Foundation

class DoKitUtil {
    
    var fileSize : UInt64 = 0
    static func openAppSetting() {
        let url = URL(string: UIApplication.openSettingsURLString)
        if let url = url {
            if UIApplication.shared.canOpenURL(url) {
                if #available(iOS 10, *) {
                    UIApplication.shared.open(url, options: [:], completionHandler: nil)
                }else{
                    UIApplication.shared.openURL(url)
                }
                
            }
        }
    }
    
    func getFileSizeWithPath(path: String) {
        let path = path
        let fileM = FileManager.default
        var isDir: ObjCBool = false
        let isExist = fileM.fileExists(atPath: path, isDirectory: &isDir)
        if isExist {
            if isDir.boolValue {
                do {
                    let dirArray = try fileM.contentsOfDirectory(atPath: path)
                    for str in dirArray {
                        let nsPath = path as NSString
                        let subUrl = nsPath.appendingPathComponent(str)
                        self.getFileSizeWithPath(path: subUrl)
                    }
                }catch{
                    print("error : \(error)")
                }
            }else{
                do {
                    let dict = try fileM.attributesOfItem(atPath: path)
                    let size = dict[FileAttributeKey.size] as! UInt64
                    fileSize += size
                }catch{
                    print("error : \(error)")
                }
                
            }
        }else{
            print("file not exist")
        }
    }
    
    static func clearLocalDatas() {
        let globalQueue = DispatchQueue.global()
        globalQueue.async {
            let homePath = NSHomeDirectory()
            let folders = ["Documents","Library","tmp"]
            for folder in folders{
                let nsHomePath = homePath as NSString
                self.clearFileWithPath(path: nsHomePath.appendingPathComponent(folder))
            }
        }
    }
    
    static func clearFileWithPath(path: String){
        let fileM = FileManager.default
        let files = fileM.subpaths(atPath: path)
        if let files = files {
            for file in files {
                let nsPath = path as NSString
                let subUrl = nsPath.appendingPathComponent(file)
                if fileM.fileExists(atPath: subUrl) {
                    do{
                        try fileM.removeItem(atPath: subUrl)
                    }catch{
                        print("remove file error = \(error)")
                    }
                    
                }
            }
        }

    }
}
