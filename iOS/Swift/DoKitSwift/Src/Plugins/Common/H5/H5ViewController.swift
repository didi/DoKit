//
//  H5ViewController.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

private let Identifier = "HistoryURLCell"

class H5ViewController: BaseViewController {
    var h5UrlTextView: UITextView!
    var lineView: UIView!
    var jumpBtn: UIButton!
    var scanJumpBtn: UIButton!
    
    // MARK: - Life Cycle Methods
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setTitle(title: LocalizedString("H5任意门"))
        
        initUI()
    }
    
    // MARK: - Override Methods
    override func needBigTitleView() -> Bool {
        return true
    }
    
    // MARK: - Target Methods
    @objc func clickScanBtnClickHandle() -> Void {
        if DoKitUtil.isSimulator() {
            ToastUtil.showToastBlack(LocalizedString("模拟器不支持扫码功能"), superView: self.view)
            return
        }
        
        let vc = QRCodeViewController()
        weak var weakSelf = self
        vc.QRCodeBlock = { QRCodeResult in
            weakSelf?.h5UrlTextView.text = QRCodeResult
            weakSelf?.jumpBtnClickHandle()
        }
        let nav = UINavigationController(rootViewController: vc)
        nav.modalPresentationStyle = .fullScreen
        self.present(nav, animated: true, completion: nil)
    }
    
    @objc func jumpBtnClickHandle() -> Void {
        if h5UrlTextView.text.isBlack {
            ToastUtil.showToastBlack(LocalizedString("链接不能为空"), superView: self.view)
            return
        }
        
        if NSURL(string: h5UrlTextView.text) == nil {
            ToastUtil.showToastBlack(LocalizedString("H5链接有误"), superView: self.view)
            return
        }
        
        let h5Url = h5UrlTextView.text
        
        let vc = DefaultWebViewController()
        vc.url = urlCorrectionWithURL(URL: h5Url ?? "")
        self.navigationController?.pushViewController(vc, animated: true)
    }
    
    // MARK: - Private Methods
    func urlCorrectionWithURL(URL: String) -> String {
        if URL.isBlack {
            return URL
        }
        
        if !URL.hasPrefix("http://") && !URL.hasPrefix("https://") {
            return "https://\(URL)"
        }
        
        if URL.hasPrefix(":") {
            return "https\(URL)"
        }
        
        if URL.hasPrefix("//") {
            return "https:\(URL)"
        }
        
        if URL.hasPrefix("/") {
            return "https:/\(URL)"
        }
        return URL
    }
}

// MARK: - UI
extension H5ViewController {
    func initUI() -> Void {
        // h5UrlTextView
        h5UrlTextView = UITextView(frame: CGRect(x: 0.0, y: bigTitleView!.bottom + kSizeFrom750_Landscape(32.0), width:view.width , height: kSizeFrom750_Landscape(358)))
        h5UrlTextView.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(32))
        h5UrlTextView.keyboardType = .URL
        h5UrlTextView.autocorrectionType = .no
        h5UrlTextView.keyboardAppearance = .dark
        h5UrlTextView.autocapitalizationType = .none
        self.view.addSubview(h5UrlTextView)
        
        // lineView
        lineView = UIView(frame: CGRect(x: 0.0, y: h5UrlTextView.bottom, width: view.width, height: kSizeFrom750_Landscape(1.0)))
        lineView.backgroundColor = UIColor.line()
        self.view.addSubview(lineView)
        
        // jumpBtn
        jumpBtn = UIButton(frame: CGRect(x: kSizeFrom750_Landscape(30.0), y: view.height - kSizeFrom750_Landscape(130.0), width: view.width - 2 * kSizeFrom750_Landscape(30.0), height: kSizeFrom750_Landscape(100.0)))
        jumpBtn.layer.cornerRadius = kSizeFrom750_Landscape(8.0)
        jumpBtn.backgroundColor = UIColor.init(0x337CC4)
        jumpBtn.setTitle(LocalizedString("点击跳转"), for: .normal)
        jumpBtn.addTarget(self, action: #selector(jumpBtnClickHandle), for: .touchUpInside)
        self.view.addSubview(jumpBtn)
        
        // scanJumpBtn
        scanJumpBtn = UIButton(frame: CGRect(x: view.width - kSizeFrom750_Landscape(71.8), y: lineView.top - kSizeFrom750_Landscape(71.8), width: kSizeFrom750_Landscape(38.6), height: kSizeFrom750_Landscape(38.6)))
        scanJumpBtn.setBackgroundImage(UIImage("doraemon_scan"), for: .normal)
        scanJumpBtn.addTarget(self, action: #selector(clickScanBtnClickHandle), for: .touchUpInside)
        self.view.addSubview(scanJumpBtn)
    }
}
