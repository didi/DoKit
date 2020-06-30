//
//  DoKitUtil.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/26.
//

import Foundation

protocol SharedProtocal {}
extension URL: SharedProtocal {}
extension String: SharedProtocal {}
extension UIImage: SharedProtocal {}

class DoKitUtil {
    
    var fileSize : UInt64 = 0
    static func openAppSetting() {
        guard let url = URL(string: UIApplication.openSettingsURLString),
            UIApplication.shared.canOpenURL(url) else {
            return
        }
        
        if #available(iOS 10, *) {
            UIApplication.shared.open(url, options: [:], completionHandler: nil)
        }else{
            UIApplication.shared.openURL(url)
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
    
    static let dateFormatter: DateFormatter = {
        $0.dateFormat = "yyyy-MM-dd HH:mm:ss"
        $0.timeZone = TimeZone.current
        return $0
    }(DateFormatter())
    
//    时间转时间格式字符串
    static func dateString(date:Date) -> String {
        let dateString = dateFormatter.string(from: date)
        return dateString
    }
    
    static func dateString(interval:TimeInterval) -> String {
        let date = Date.init(timeIntervalSince1970: interval)
        return dateString(date: date)
    }
    
    static func dateFormatNow() -> String {
        return dateFormatter.string(from: Date())
    }
    
    static func share(obj: SharedProtocal, from: UIViewController) {
        let controller = UIActivityViewController(activityItems: [obj], applicationActivities: nil)
        if AppInfoUtil.isIpad {
            controller.popoverPresentationController?.sourceView = from.view
            controller.popoverPresentationController?.sourceRect = CGRect.init(x: 0, y: 0, width: kScreenWidth, height: 400)
        }
        from.present(controller, animated: true, completion: nil)
    }
}

// MARK:- 分享
extension DoKitUtil {
    
    static func share(with image: UIImage, _ controller: UIViewController,completion: ((_ : Bool) -> Swift.Void)? = nil) {
        _share(with: image, controller, completion: completion)
    }
    
    static func share(with text: String, _ controller: UIViewController, completion: ((_ : Bool) -> Swift.Void)? = nil) {
        _share(with: text, controller, completion: completion)
    }
    
    static func share(with url: URL, _ controller: UIViewController, completion: ((_ : Bool) -> Swift.Void)? = nil) {
        _share(with: url, controller, completion: completion)
    }
    
    private static func _share(with object: Any, _ controller: UIViewController, completion: ((_ : Bool) -> Void)?) {
        let activity = UIActivityViewController(activityItems: [object], applicationActivities: nil)
        activity.completionWithItemsHandler = {
            (type, result, returnedItems, error) in
            completion?(result)
        }
        
        if Device.isPad {
            activity.popoverPresentationController?.sourceView = controller.view
        } else {
            controller.present(activity, animated: true)
        }
    }
}
