//
//  QRScanView.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit
import AVFoundation

protocol QRScanDelegate: NSObjectProtocol {
    /// 扫描有结果回调方法，如需结束在回调里调用stopScanning方法
    /// - Parameters:
    ///   - scanView: 回调的View
    ///   - message: 结果字符串
    func scanViewPickUpMessage(_ scanView: QRScanView, message: String)
    
    
    /// 获取周围光线强弱
    /// - Parameters:
    ///   - scanView: 回调的view
    ///   - brightnessValue: 光线数值，越小越暗
    func scanViewAroundBrightness(_ scanView: QRScanView, brightnessValue: String)
}

private var scanTime: CGFloat = 3.0
private var borderLineWidth: CGFloat = 0.5
private var cornerLineWidth: CGFloat = 1.5
private var scanLineWidth: CGFloat = 42
private let scanLineAnimationName = "scanLineAnimation"

class QRScanView: UIView {
    /// 可自定义的蒙版View，可在上面添加自定义控件,也可以改变背景颜色，透明度
    /// 默认为50%透明度黑色，遮盖区域依赖scanRect,需先指定scanRect，否则为默认
    private var _cMaskView: UIView?
    var cMaskView: UIView? {
        set {
            if newValue != nil {
                _cMaskView = newValue
            }
        }
        get {
            if _cMaskView == nil {
                _cMaskView = UIView(frame: bounds)
                _cMaskView!.backgroundColor = UIColor(red: 0.0, green: 0.0, blue: 0.0, alpha: 0.5)
                let fullBezierPath = UIBezierPath(rect: bounds)
                let scanBezierPath = UIBezierPath(rect: scanRect)
                fullBezierPath.append(scanBezierPath.reversing())
                let shapeLayer = CAShapeLayer()
                shapeLayer.path = fullBezierPath.cgPath
                _cMaskView!.layer.mask = shapeLayer
            }
            return _cMaskView
        }
    }
    /// 上下移动的扫描线的颜色，默认为橙色
    var scanLineColor: UIColor = .orange()
    /// 四角的线的颜色，默认为橙色
    var cornerLineColor: UIColor = .orange()
    /// 扫描边框的颜色，默认为白色
    var borderLineColor: UIColor = .white
    /// 是否显示上下移动的扫描线，默认为YES
    var isShowScanLine = true
    /// 是否显示边框，默认为NO
    var isShowBorderLine = false
    /// 是否显示四角，默认为YES
    var isShowCornerLine = true

    weak var delegate: QRScanDelegate?
    /// 扫描区域的Frame，默认长宽为Frame的宽度3/4，位置为Frame中心
    private var _scanRect: CGRect!
    var scanRect: CGRect! {
        set {
            if !newValue.isEmpty {
                _scanRect = newValue
            }
        }
        
        get {
            if _scanRect.isEmpty {
                let scanSize = CGSize(width: frame.size.width * 3 / 4, height: frame.size.width * 3 / 4)
                _scanRect = CGRect(x: (frame.size.width - scanSize.width) / 2, y: (frame.size.height - scanSize.height) / 2, width: scanSize.width, height: scanSize.height)
            }
            return _scanRect
        }
    }
    
    /// 首次申请相机权限-系统弹框点击不允许
    var forbidCameraAuth: (() -> Void)?
    /// 相机权限弹框-点击暂不开启
    var unopenCameraAuth: (() -> Void)?

    private var device: AVCaptureDevice?
    private var deviceInput: AVCaptureDeviceInput?
    private var dataOutput: AVCaptureMetadataOutput?
    private var session: AVCaptureSession?

    private var _middleView: UIView?
    private var middleView: UIView? {
        if _middleView == nil {
            _middleView = UIView(frame: _scanRect)
            _middleView?.clipsToBounds = true
        }
        return _middleView
    }

    private var _scanLine: UIView?
    private var scanLine: UIView? {
        if _scanLine == nil {
            _scanLine = UIView(frame: CGRect(x: 0, y: 0, width: _scanRect.size.width, height: scanLineWidth))
            _scanLine?.isHidden = true
            let gradient = CAGradientLayer()
            gradient.startPoint = CGPoint(x: 0.5, y: 0)
            gradient.endPoint = CGPoint(x: 0.5, y: 1)
            gradient.frame = _scanLine?.layer.bounds ?? CGRect.zero
            gradient.colors = [
                scanLineColor.withAlphaComponent(0).cgColor,
                scanLineColor.withAlphaComponent(0.4).cgColor,
                scanLineColor.cgColor
            ].compactMap { $0 }
            gradient.locations = [NSNumber(value: 0), NSNumber(value: 0.96), NSNumber(value: 0.97)]
            _scanLine?.layer.addSublayer(gradient)
        }
        return _scanLine
    }
    private var previewLayer: AVCaptureVideoPreviewLayer?
    private var videoDataOutput: AVCaptureVideoDataOutput?
    
    // MARK: - Public Methods
    /// 第一次调用会初始化相机相关并开始扫描
    /// 之后调用，可在暂停后恢复
    func startScanning() {
        if !statusCheck() {
            return
        }
        
        guard self.session != nil else {
            setupViews()
            configCameraAndStart()
            return
        }
        
        if self.session!.isRunning {
            return
        }
        self.session!.startRunning()
        showScanLine(showScanLine: isShowScanLine)
    }
    
    /// 停止扫描
    func stopScanning() {
        if !(self.session?.isRunning ?? false) {
            return
        }
        self.session!.stopRunning()
        // 自动开启手电筒后，在执行了stopRunning时系统会关闭手电筒，这时重新打开手电筒，效果会闪一下
        if self.device!.torchMode == .on {
            do {
                try self.device!.lockForConfiguration()
            } catch let error as NSError {
                print("error: \(error)")
            }
            self.device!.torchMode = .on
            self.device!.unlockForConfiguration()
        }
        showScanLine(showScanLine: false)
    }
    
    // MARK: - Private Methods
    private func configCameraAndStart() {
        DispatchQueue.global(qos: .default).async(execute: {
            self.device = AVCaptureDevice.default(for: .video)
            do {
               try self.deviceInput = AVCaptureDeviceInput(device: self.device!)
            } catch let error as NSError {
                print("\(error)")
            }
            
            self.dataOutput = AVCaptureMetadataOutput()
            self.dataOutput!.setMetadataObjectsDelegate(self, queue: .main)

            self.session = AVCaptureSession()
            if self.device!.supportsSessionPreset(.hd1920x1080) {
                self.session!.sessionPreset = .hd1920x1080
            } else {
                self.session!.sessionPreset = .high
            }
            if self.session!.canAddInput(self.deviceInput!) {
                self.session!.addInput(self.deviceInput!)
            }
            if self.session!.canAddOutput(self.dataOutput!) {
                self.session!.addOutput(self.dataOutput!)
            }

            if self.dataOutput!.availableMetadataObjectTypes.contains(AVMetadataObject.ObjectType.qr) {
                self.dataOutput!.metadataObjectTypes = [.qr]
            }

            // 获取光线强弱
            self.videoDataOutput = AVCaptureVideoDataOutput()
            self.videoDataOutput!.setSampleBufferDelegate(self, queue: DispatchQueue.main)

            if self.session!.canAddOutput(self.videoDataOutput!) {
                self.session!.addOutput(self.videoDataOutput!)
            }
            DispatchQueue.main.async(execute: {
                self.previewLayer = AVCaptureVideoPreviewLayer(session: self.session!)
                self.previewLayer!.videoGravity = .resizeAspectFill
                self.previewLayer!.frame = self.frame
                self.layer.insertSublayer(self.previewLayer!, at: 0)
                self.session!.startRunning()
                self.dataOutput!.rectOfInterest = self.previewLayer!.metadataOutputRectConverted(fromLayerRect: self.scanRect)
                self.showScanLine(showScanLine: self.isShowScanLine)
            })
        })
    }
    
    private func showScanLine(showScanLine: Bool) {
        if showScanLine {
            addScanLineAnimation()
        } else {
            removeScanLineAnimation()
        }
    }
    
    private func statusCheck() -> Bool {
        if !UIImagePickerController.isSourceTypeAvailable(.camera) {
            AlertUtil.handleAlertAction(vc: viewController, text: LocalizedString("设备无相机——设备无相机功能，无法进行扫描")) {}
            return false
        }
        
        if !UIImagePickerController.isCameraDeviceAvailable(.front) && !UIImagePickerController.isCameraDeviceAvailable(.rear) {
            AlertUtil.handleAlertAction(vc: viewController, text: LocalizedString("设备相机错误——无法启用相机，请检查")) {}
            return false
        }
        
        if !isCameraAuthStatusCorrect() {
            AlertUtil.handleAlertAction(vc: viewController, text: LocalizedString("相机权限未开启，请到「设置-隐私-相机」中允许DoKit访问您的相机")) {}
            return false
        }
        return true
    }
    
    private func setupViews() {
        self.addSubview(self.middleView!)
        self.addSubview(self.cMaskView!)
        self.middleView?.addSubview(self.scanLine!)
        
        if self.isShowCornerLine {
            addCornerLines()
        }
        
        if self.isShowBorderLine {
            addScanBorderLine()
        }
    }
      
    private func isCameraAuthStatusCorrect() -> Bool {
        let authStatus: AVAuthorizationStatus = AVCaptureDevice.authorizationStatus(for: .video)
        if authStatus == .authorized {
            return true
        } else if authStatus == .notDetermined {
            AVCaptureDevice.requestAccess(for: .video, completionHandler: { granted in
             if !granted {
                 DispatchQueue.main.async(execute: {
                    if (self.forbidCameraAuth != nil) {
                        self.forbidCameraAuth!()
                     }
                 })
             }
         })
         return true
        }
        return false
    }
    
    private func addScanLineAnimation() {
        self.scanLine?.isHidden = false
        let animation = CABasicAnimation(keyPath: "transform.translation.y")
        animation.fromValue = -scanLineWidth
        animation.toValue = self.scanRect.size.height - scanLineWidth
        animation.duration = CFTimeInterval(scanTime)
        animation.repeatCount = Float(OPEN_MAX)
        animation.fillMode = .forwards
        animation.isRemovedOnCompletion = false
        animation.timingFunction = CAMediaTimingFunction(name: .easeOut)
        self.scanLine?.layer.add(animation, forKey: scanLineAnimationName)
    }
    
    private func removeScanLineAnimation() {
        self.scanLine?.layer.removeAnimation(forKey: scanLineAnimationName)
        self.scanLine?.isHidden = true
    }
    
    // MARK: - bezierPath
    private func addScanBorderLine() {
        let borderRect = CGRect(x: scanRect.origin.x + borderLineWidth, y: scanRect.origin.y + borderLineWidth, width: scanRect.size.width - 2 * borderLineWidth, height: scanRect.size.height - 2 * borderLineWidth)
        let scanBezierPath = UIBezierPath(rect: borderRect)
        let lineLayer = CAShapeLayer()
        lineLayer.path = scanBezierPath.cgPath
        lineLayer.lineWidth = borderLineWidth
        lineLayer.strokeColor = borderLineColor.cgColor
        lineLayer.fillColor = UIColor.clear.cgColor
        layer.addSublayer(lineLayer)
    }
    
    private func addCornerLines() {
        let lineLayer = CAShapeLayer()
        lineLayer.lineWidth = cornerLineWidth
        lineLayer.strokeColor = cornerLineColor.cgColor
        lineLayer.fillColor = UIColor.clear.cgColor
        let halfLineLong: CGFloat = scanRect.size.width / 12
        let lineBezierPath = UIBezierPath()

        let spacing: CGFloat = cornerLineWidth / 2

        let leftUpPoint = CGPoint(x: scanRect.origin.x + spacing, y: scanRect.origin.y + spacing)
        lineBezierPath.move(to: CGPoint(x: leftUpPoint.x, y: leftUpPoint.y + halfLineLong))
        lineBezierPath.addLine(to: leftUpPoint)
        lineBezierPath.addLine(to: CGPoint(x: leftUpPoint.x + halfLineLong, y: leftUpPoint.y))
        lineLayer.path = lineBezierPath.cgPath
        layer.addSublayer(lineLayer)


        let leftDownPoint = CGPoint(x: scanRect.origin.x + spacing, y: scanRect.origin.y + scanRect.size.height - spacing)
        lineBezierPath.move(to: CGPoint(x: leftDownPoint.x, y: leftDownPoint.y - halfLineLong))
        lineBezierPath.addLine(to: leftDownPoint)
        lineBezierPath.addLine(to: CGPoint(x: leftDownPoint.x + halfLineLong, y: leftDownPoint.y))
        lineLayer.path = lineBezierPath.cgPath
        layer.addSublayer(lineLayer)

        let rightUpPoint = CGPoint(x: scanRect.origin.x + scanRect.size.width - spacing, y: scanRect.origin.y + spacing)
        lineBezierPath.move(to: CGPoint(x: rightUpPoint.x - halfLineLong, y: rightUpPoint.y))
        lineBezierPath.addLine(to: rightUpPoint)
        lineBezierPath.addLine(to: CGPoint(x: rightUpPoint.x, y: rightUpPoint.y + halfLineLong))
        lineLayer.path = lineBezierPath.cgPath
        layer.addSublayer(lineLayer)
        
        let rightDownPoint = CGPoint(x: scanRect.origin.x + scanRect.size.width - spacing, y: scanRect.origin.y + scanRect.size.height - spacing)
        lineBezierPath.move(to: CGPoint(x: rightDownPoint.x - halfLineLong, y: rightDownPoint.y))
        lineBezierPath.addLine(to: rightDownPoint)
        lineBezierPath.addLine(to: CGPoint(x: rightDownPoint.x, y: rightDownPoint.y - halfLineLong))
        lineLayer.path = lineBezierPath.cgPath
        layer.addSublayer(lineLayer)
    }
}

// MARK: - AVCaptureMetadataOutputObjectsDelegate
extension QRScanView: AVCaptureMetadataOutputObjectsDelegate {
    func metadataOutput(_ output: AVCaptureMetadataOutput, didOutput metadataObjects: [AVMetadataObject], from connection: AVCaptureConnection) {
        if metadataObjects.count == 0 {
            return
        }
        
        stopScanning()
        
        var result: String? = nil
        let firstObject = metadataObjects.first as! AVMetadataMachineReadableCodeObject
        result = firstObject.stringValue
        
        delegate?.scanViewPickUpMessage(self, message: result!)
    }
}

// MARK: - AVCaptureVideoDataOutputSampleBufferDelegate
extension QRScanView: AVCaptureVideoDataOutputSampleBufferDelegate {
    func captureOutput(_ output: AVCaptureOutput, didOutput sampleBuffer: CMSampleBuffer, from connection: AVCaptureConnection) {
        let metadataDict = CMCopyDictionaryOfAttachments(allocator: nil, target: sampleBuffer, attachmentMode: kCMAttachmentMode_ShouldPropagate)
        let metadata = metadataDict as? [AnyHashable : Any]
        let exifMetadata = (metadata?[kCGImagePropertyExifDictionary as String]) as? [AnyHashable : Any]
        var brightnessValue: String? = nil
        if let object = exifMetadata?[kCGImagePropertyExifBrightnessValue as String] {
            brightnessValue = "\(object)"
        }
        delegate?.scanViewAroundBrightness(self, brightnessValue: brightnessValue!)
    }
}
