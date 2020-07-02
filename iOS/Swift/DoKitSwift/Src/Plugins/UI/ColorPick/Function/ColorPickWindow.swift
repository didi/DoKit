//
//  ColorPickWindow.swift
//  DoraemonKit-Swift
//
//  Created by 方林威 on 2020/5/28.
//

import UIKit

fileprivate let windowSize: CGFloat = 150
class ColorPickWindow: UIWindow {
    
    static let shared = ColorPickWindow(frame:
        CGRect(x: kScreenWidth / 2 - windowSize / 2,
               y: kScreenHeight / 2 - windowSize / 2,
               width: windowSize,
               height: windowSize
        )
    )
    
    private lazy var magnifyLayer: ColorPickMagnifyLayer = {
        $0.contentsScale = UIScreen.main.scale
        $0.frame = bounds
        $0.pointColorCallBack = { potin in
            return self.screenShotImage?.toColor(point: potin) ?? .blue
        }
        return $0
    }( ColorPickMagnifyLayer() )
    
    private var screenShotImage: UIImage?
    
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
        rootViewController = rootViewController ?? UIViewController()
        
        addGestureRecognizer(UIPanGestureRecognizer(target: self, action: #selector(panAction)))
        
        layer.addSublayer(magnifyLayer)
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(closePluginNotification),
            name: .closePluginNotification,
            object: nil
        )
    }
}

// MARK: - Public
extension ColorPickWindow {
    
    func show() {
        isHidden = false
    }
    
    func hide() {
        isHidden = true
    }
}

// MARK: - Actions
extension ColorPickWindow {
    
    @objc
    private func panAction(_ sender: UIPanGestureRecognizer) {
        guard let view = sender.view else {
            return
        }
        if (sender.state == .began) {
            // 开始拖动的时候更新屏幕快照
            updateScreeShotImage()
        }
        
        //1、获得拖动位移
        let point = sender.translation(in: view)
        //2、清空拖动位移
        sender.setTranslation(.zero, in: view)
        //3、重新设置控件位置
        let x = view.centerX + point.x
        let y = view.centerY + point.y
        
        CATransaction.begin()
        CATransaction.setDisableActions(false)
        let centerPoint = CGPoint(x: x, y: y)
        view.center = centerPoint
        magnifyLayer.targetPoint = centerPoint
        // Make magnifyLayer sharp on screen
        let magnifyFrame = magnifyLayer.frame
        magnifyLayer.frame.origin = CGPoint(x: magnifyFrame.origin.x.rounded(), y: magnifyFrame.origin.y.rounded())
        magnifyLayer.setNeedsDisplay()
        CATransaction.commit()
        if let color = screenShotImage?.toColor(point: centerPoint) {
            VisualInfo.colorPick.view.set(current: color)
        }
    }
    
    @objc
    private func closePluginNotification() {
        hide()
    }
}

// MARK: - Private
extension ColorPickWindow {
    
    private func updateScreeShotImage() {
        UIGraphicsBeginImageContext(UIScreen.main.bounds.size)
        if let ctx = UIGraphicsGetCurrentContext() {
            getKeyWindow()?.layer.render(in: ctx)
        }
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        screenShotImage = image
    }
}

extension UIImage {
    
    fileprivate func toColor(point: CGPoint) -> UIColor {
        // Cancel if point is outside image coordinates
        guard CGRect(x: 0, y: 0, width: size.width, height: size.height).contains(point) else {
            return .clear
        }
        guard let inputCGImage = cgImage else {
            return .clear
        }
        // Create a 1x1 pixel byte array and bitmap context to draw the pixel into.
        // Reference: http://stackoverflow.com/questions/1042830/retrieving-a-pixel-alpha-value-for-a-uiimage
        let width = CGFloat(inputCGImage.width)
        let height = CGFloat(inputCGImage.height)
        
        var pixel = [UInt8](repeatElement(0, count: 4))
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bitmapInfo = CGImageAlphaInfo.premultipliedLast.rawValue | CGBitmapInfo.byteOrder32Big.rawValue
        
        guard let context = CGContext(
            data: &pixel,
            width: 1,
            height: 1,
            bitsPerComponent: 8,
            bytesPerRow: 4,
            space: colorSpace,
            bitmapInfo: bitmapInfo) else {
            return .clear
        }
        context.setBlendMode(.copy)
        context.translateBy(x: -trunc(point.x), y: trunc(point.y) - height)
        context.draw(inputCGImage, in: CGRect(x: 0, y: 0, width: width, height: height))
        
        let r = pixel[0]
        let g = pixel[1]
        let b = pixel[2]
        let a = pixel[3]
        return UIColor(red: CGFloat(r)/255.0,
                       green: CGFloat(g)/255.0,
                       blue: CGFloat(b)/255.0,
                       alpha: CGFloat(a)/255.0)
    }
}
