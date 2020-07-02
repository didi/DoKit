//
//  ColorPickInfoWindow.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import UIKit

class ColorPickInfoWindow: UIWindow {

    static let shared = ColorPickInfoWindow(frame: ColorPickInfoWindow.rect)
    
    private lazy var pickInfoView = ColorPickInfoView()
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setup()
    }
    
   required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    private func setup() {
        if #available(iOS 13, *) {
            if
                let scene = UIApplication.shared.connectedScenes.first(where: { $0.activationState == .foregroundActive }),
                let windowScene = scene as? UIWindowScene {
                self.windowScene = windowScene
            }
        }
        
        backgroundColor = .clear
        windowLevel = .statusBar + 1.1
        rootViewController = rootViewController ?? ColorPickInfoController()
        rootViewController?.view.addSubview(pickInfoView)
        
        addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(panAction)))
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(closePluginNotification),
            name: .closePluginNotification,
            object: nil)
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        pickInfoView.frame = bounds
    }
}

// MARK: - Public
extension ColorPickInfoWindow {
    
    func show() {
        isHidden = false
    }
    
    func hide() {
        isHidden = true
    }
    
    func set(current color: UIColor) {
        pickInfoView.set(current: color)
    }
}

// MARK: - Actions
extension ColorPickInfoWindow {
    
    @objc
    private func panAction(_ sender: UIPanGestureRecognizer) {
        guard let view = sender.view else { return }
        //1、获得拖动位移
        let offsetPoint = sender.translation(in: view)
        //2、清空拖动位移
        sender.setTranslation(.zero, in: view)
        //3、重新设置控件位置
        view.center = CGPoint(
            x: view.centerX + offsetPoint.x,
            y: view.centerY + offsetPoint.y
        )
    }
    
    @objc
    private func closePluginNotification() {
        hide()
    }
}

// MARK: - Private
extension ColorPickInfoWindow {
    
    static private var rect: CGRect {
        let height: CGFloat = kSizeFrom750_Landscape(100)
        let margin: CGFloat = kSizeFrom750_Landscape(30)
        switch kOrientationPortrait {
        case true:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenWidth - 2 * margin,
                height: height
            )
            
        case false:
            return CGRect(
                x: margin,
                y: kScreenHeight - height - margin - kIphoneSafeBottomAreaHeight,
                width: kScreenHeight - 2 * margin,
                height: height
            )
        }
    }
}

fileprivate class ColorPickInfoController: UIViewController {
    
    override func viewWillTransition(to size: CGSize, with coordinator: UIViewControllerTransitionCoordinator) {
        super.viewWillTransition(to: size, with: coordinator)
        DispatchQueue.main.async {
            let x: CGFloat = kSizeFrom750_Landscape(30)
            self.view.window?.frame = CGRect(
                x: x,
                y: kScreenHeight - x - min(size.width, size.height),
                width: size.height,
                height: size.width
            )
        }
    }
}
