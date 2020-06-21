//
//  DoKitUtil.swift
//  DoraemonKit-Swift
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
    
    static func isSimulator() -> Bool {
        var isSim = false
        #if arch(i386) || arch(x86_64)
            isSim = true
        #endif
        return isSim
    }
    

    // 分享图片
    static func shareImage(image:UIImage,fromViewController:UIViewController) {
        share(item: image, fromViewController:fromViewController)
    }
    // 分享地址
    static func shareURL(url:URL,fromViewController:UIViewController) {
        share(item: url, fromViewController:fromViewController)
    }
//    分享文字
    static func shareString(content:String,fromViewController:UIViewController) {
        share(item: content, fromViewController: fromViewController)
    }
    
    static func share(item:Any,fromViewController:UIViewController) {
        let activityViewController = UIActivityViewController.init(activityItems: [item], applicationActivities: nil)
        if isIpad() {
            let popOver = activityViewController.popoverPresentationController
            popOver?.sourceView = fromViewController.view
            popOver?.sourceRect = CGRect(x: 0, y: 0, width: kScreenWidth, height: 340)
        }
        fromViewController.present(activityViewController, animated: true, completion: nil)
        
    }
    
//    时间转时间格式字符串
    static func dateString(date:Date) -> String {
        let format = "yyyy-MM-dd HH:mm:ss"
        let formatter = DateFormatter()
        formatter.dateFormat = format
        let dateString = formatter.string(from: date)
        return dateString
    }
    
    static func dateString(interval:TimeInterval) -> String {
        let date = Date.init(timeIntervalSince1970: interval)
        return dateString(date: date)
    }
    
    static func isIpad() -> Bool {
        return UIDevice.current.userInterfaceIdiom == .pad
    }
    
}
