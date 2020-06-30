//
//  MainThreadCheckerManager.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/6/22.
//

import Foundation

class MainThreadCheckerManager {
    
    static let `default` = MainThreadCheckerManager()
    private init() {}
    
    @Store("mainThreadChecker", defaultValue: true)
    static var mainThreadChecker: Bool
    
    lazy var directory: String = {
        let dir = NSHomeDirectory() + "/Library/Caches/Dokit/MainThreadChecker"
        if !FileManager.default.fileExists(atPath: dir){
            try? FileManager.default.createDirectory(atPath: dir, withIntermediateDirectories: true, attributes: nil)
        }
        return dir
    }()
    
    static let swizzleIfNeeded: () = {
        UIView.swizzle(originalSelector: #selector(UIView.setNeedsLayout), swizzledSelector: #selector(UIView.dokit_setNeedsLayout))
        //UIView.swizzle(originalSelector: #selector(UIView.setNeedsDisplay), swizzledSelector: #selector(UIView.dokit_setNeedsDisplay))
        UIView.swizzle(originalSelector: #selector(UIView.setNeedsDisplay(_:)), swizzledSelector: #selector(UIView.dokit_setNeedsDisplay(_:)))
    }()
    
}

fileprivate extension UIView {

    @objc func dokit_setNeedsLayout(){
        self.dokit_setNeedsLayout()
        self.checkUI()
    }
    
    @objc func dokit_setNeedsDisplay(){
        self.dokit_setNeedsDisplay()
        self.checkUI()
    }
    
    @objc func dokit_setNeedsDisplay(_ rect: CGRect){
        self.dokit_setNeedsDisplay(rect)
        self.checkUI()
    }
    func checkUI(){
        if Thread.isMainThread {return}
        if !MainThreadCheckerManager.mainThreadChecker {return}
        let fileName = DoKitUtil.dateFormatNow()
        let str = """
        Time:\(fileName)\n\n\n
        SubThread:
        \(backtraceCurrentThread())\n\n\n
        MainThread:
        \(backtraceMainThread())
        """
        let path = MainThreadCheckerManager.default.directory + "/" + fileName + ".txt"
        do{
            try str.write(toFile: path, atomically: true, encoding: .utf8)
        }catch {
            print("子线程UI日子写入文件出现错误:\(error)")
        }
    }
}
