//
//  DoKitMainThreadCheckerPlugin.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

struct MainThreadCheckerPlugin: Plugin {
    
    var module: String { LocalizedString("常用工具") }
    
    var title: String { LocalizedString("子线程UI") }
    
    var icon: UIImage? {
        return DKImage(named: "doraemon_ui")
    }
    
    func onInstall() {
        if MainThreadCheckerPlugin.mainThreadChecker {
            MainThreadCheckerPlugin.swizzleIfNeeded
        }
    }
    
    func onSelected() {
        let vc = MainThreadCheckerViewController()
        HomeWindow.shared.openPlugin(vc: vc)
    }
    
    @Store("mainThreadChecker", defaultValue: true)
    static var mainThreadChecker: Bool
    
    static let swizzleIfNeeded: () = {
        UIView.swizzle(originalSelector: #selector(UIView.setNeedsLayout), swizzledSelector: #selector(UIView.dokit_setNeedsLayout))
        //UIView.swizzle(originalSelector: #selector(UIView.setNeedsDisplay), swizzledSelector: #selector(UIView.dokit_setNeedsDisplay))
        UIView.swizzle(originalSelector: #selector(UIView.setNeedsDisplay(_:)), swizzledSelector: #selector(UIView.dokit_setNeedsDisplay(_:)))
    }()
}


extension UIView {

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
        if !Thread.isMainThread{
            print("\(self)触发了子线程渲染")
            print(backtraceAllThread().joined(separator: "\n\n\n======================\n\n\n"))
//            print("\n\n\n======================\n\n\n")
//            print(backTraceCurrentThread())
//            print("\n\n\n======================\n\n\n")
//            print(backTraceAllThread().joined(separator: "\n\n\n======================\n\n\n"))
        }
    }
}
