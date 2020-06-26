//
//  UIViewController+UIProfile.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

// MARK: - UIProfile

extension UIViewController {
    
    func profileViewDepth() {
        guard UIProfileManager.shared.isEnable else { return }
        travelView(view: view, depth: 0)
        showUIProfile()
    }
    
    func resetProfileData() {
        self.dokitDepth = 0
        self.dokitDepthView?.layer.borderWidth = 0
        self.dokitDepthView?.layer.borderColor = nil
    }
    
    private func travelView(view: UIView, depth: Int) {
        let newDepth = depth + 1
        if newDepth > self.dokitDepth {
            self.dokitDepth = newDepth
            self.dokitDepthView = view
        }
        guard view.subviews.count > 0 else { return }
        view.subviews.forEach { subview in
            travelView(view: subview, depth: newDepth)
        }
    }
    
    private func showUIProfile() {
        guard let view = dokitDepthView else { return }
        let text = String(format: "[%d][%@]", self.dokitDepth, NSStringFromClass(type(of: view)))
        var classNames = [String]()
        classNames.append(NSStringFromClass(type(of: view)))
        var tmpSuperView = view.superview
        while tmpSuperView != nil && tmpSuperView != self.view {
            classNames.append(NSStringFromClass(type(of: tmpSuperView!)))
            tmpSuperView = tmpSuperView!.superview
        }
        classNames.append(NSStringFromClass(type(of: self.view)))
        let detail = classNames.reversed().reduce("") { $0 + $1 + "\r\n"}
        UIProfileWindow.shared.show(depthText: text, detailInfo: detail)
        view.layer.borderWidth = 1
        view.layer.borderColor = UIColor.red.cgColor
    }
    
}


// MARK: -

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

// MARK: -

private var dokitDepthKey: Void?
private var dokitDepthViewKey: Void?

extension UIViewController {
    
    var dokitDepth: Int {
        get { objc_getAssociatedObject(self, &dokitDepthKey) as? Int ?? 0 }
        set { objc_setAssociatedObject(self, &dokitDepthKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC) }
    }
    
    var dokitDepthView: UIView? {
        get { objc_getAssociatedObject(self, &dokitDepthViewKey) as? UIView }
        set { objc_setAssociatedObject(self, &dokitDepthViewKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC) }
    }
}

