//
//  HomeWindow.swift
//  DoraemonKit-Swift
//
//  Created by didi on 2020/5/22.
//

import UIKit

class HomeWindow: UIWindow {
    public static let shared = HomeWindow(frame: CGRect(x: 0, y: 0, width: kScreenWidth, height: kScreenHeight))
    
    private override init(frame: CGRect) {
        super.init(frame: frame)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func show() {
        let homeVc = HomeViewController()
        self.setRootVc(rootVc: homeVc)
        self.isHidden = false
    }
    
    func hide() {
        if self.rootViewController?.presentingViewController != nil {
            self.rootViewController?.presentingViewController?.dismiss(animated: false, completion: nil)
        }
        self.setRootVc(rootVc: nil)
        self.isHidden = true
    }
    
    func setRootVc(rootVc: UIViewController?) {
        if let rootVc = rootVc {
            let nav = UINavigationController(rootViewController: rootVc)
            self.rootViewController = nav
        }else{
            self.rootViewController = nil
        }
    }
    
    func openPlugin(vc: UIViewController) {
        self.setRootVc(rootVc: vc)
    }
}
