//
//  UIViewController+UIProfile.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

// MARK: - UIProfile

private var dokitDepthKey: Void?
private var dokitDepthViewKey: Void?

extension UIViewController {
    
    func profileViewDepth() {
//        guard UIProfileManager.shared.isEnable else { return }
        travelView(view: self.view)
        showUIProfile()
    }
    
    func resetProfileData() {
        self.dokit_depth = 0
        self.dokit_view = nil
    }
    
    
    private func travelView(view: UIView) {
        travelView(view: view, depth: 0)
    }
    
    private func travelView(view: UIView, depth: Int) {
        let newDepth = depth + 1
        if newDepth > self.dokit_depth {
            self.dokit_depth = newDepth
            self.dokit_view = view
        }
        guard view.subviews.count > 0 else { return }
        view.subviews.forEach { subview in
            travelView(view: subview, depth: newDepth)
        }
    }
    
    private func showUIProfile() {
        guard let view = dokit_view else { return }
        let text = String(format: "[%d][%@]", self.dokit_depth, NSStringFromClass(type(of: view)))
        var array = [String]()
        array.append(NSStringFromClass(type(of: view)))
        var tmpSuperView = view.superview
        while tmpSuperView != nil && tmpSuperView != self.view {
            array.append(NSStringFromClass(type(of: tmpSuperView!)))
            tmpSuperView = tmpSuperView!.superview
        }
        array.append(NSStringFromClass(type(of: self.view)))
        let detail = array.reversed().reduce("") { $0 + $1 + "\r\n"}
        UIProfileWindow.shared.show(depthText: text, detailInfo: detail)
        view.layer.borderWidth = 1
        view.layer.borderColor = UIColor.red.cgColor
    }
    
    
    var dokit_depth: Int {
        get {
            objc_getAssociatedObject(self, &dokitDepthKey) as? Int ?? 0
        }
        
        set {
            objc_setAssociatedObject(self, &dokitDepthKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
    
    var dokit_view: UIView? {
        get {
            objc_getAssociatedObject(self, &dokitDepthViewKey) as? UIView
        }
        
        set {
            objc_setAssociatedObject(self, &dokitDepthViewKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
}


extension UIViewController {
    
    @objc func dokit_viewDidAppear(_ animated: Bool) {
        self.dokit_viewDidAppear(animated)
        self.profileViewDepth()
    }
    
    @objc func dokit_viewWillDisappear(_ animated: Bool) {
        self.dokit_viewWillDisappear(animated)
        self.resetProfileData()
    }
}
