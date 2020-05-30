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
    var historyTableView: UITableView!
    
    var historyArray: [String]?
    
    // MARK: - Life Cycle Methods
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.setTitle(title: LocalizedString("H5任意门"))
        
        initUI()
        
        // 监听键盘的显示和隐藏
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillShow(_:)), name: UIResponder.keyboardWillShowNotification, object: nil)
        NotificationCenter.default.addObserver(self, selector: #selector(keyboardWillHide(_:)), name: UIResponder.keyboardWillHideNotification, object: nil)
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        self.historyArray = CacheManager.shared.h5historicalRecord
        self.historyTableView.reloadData()
    }
    
    deinit {
        NotificationCenter.default.removeObserver(self)
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
        CacheManager.shared.saveH5historicalRecord(text: h5Url)
        
        if DoKit.shared.H5DoorBlock != nil {
            self.leftNavBackClick()
            DoKit.shared.H5DoorBlock!(h5Url ?? "")
        } else {
            let vc = DefaultWebViewController()
            vc.url = urlCorrectionWithURL(URL: h5Url ?? "")
            self.navigationController?.pushViewController(vc, animated: true)
        }
    }
    
    @objc func clearRecordBtnClickHandle(sender: UIButton) -> Void {
        CacheManager.shared.clearAllH5historicalRecord()
        self.historyArray = CacheManager.shared.h5historicalRecord
        self.historyTableView.reloadData()
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
    
    // MARK: - Notification
    
    // 当键盘出现或改变时调用（调整view位置，适应键盘高度，即：让view在键盘上）
    @objc func keyboardWillShow(_ aNotification: Notification?) {
        // 获取键盘的高度
        let userInfo = aNotification?.userInfo
        let aValue = userInfo?[UIResponder.keyboardFrameEndUserInfoKey] as? NSValue
        let keyboardRect = aValue?.cgRectValue
        let height = keyboardRect?.size.height ?? 0.0

        let frame = jumpBtn.frame

        let offset = height - (CGFloat(kScreenHeight) - frame.maxY)

        let duration = CGFloat((userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber)?.doubleValue ?? 0.0)
        UIView.animate(withDuration: TimeInterval(duration), animations: {
            if offset > 0 {
                self.jumpBtn.frame.origin.y = self.jumpBtn.frame.origin.y - offset
                self.view.layoutIfNeeded()
            }
        })
    }
    
    // 当键退出时调用
    @objc func keyboardWillHide(_ aNotification: Notification?) {
        let userInfo = aNotification?.userInfo
        let duration = CGFloat((userInfo?[UIResponder.keyboardAnimationDurationUserInfoKey] as? NSNumber)?.doubleValue ?? 0.0)
        UIView.animate(withDuration: TimeInterval(duration), animations: {
            self.jumpBtn.frame.origin.y = self.view.frame.size.height - kSizeFrom750_Landscape(130)
            self.view.layoutIfNeeded()
        })
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
        scanJumpBtn.setBackgroundImage(DKImage(named: "doraemon_scan"), for: .normal)
        scanJumpBtn.addTarget(self, action: #selector(clickScanBtnClickHandle), for: .touchUpInside)
        self.view.addSubview(scanJumpBtn)
        
        // HistoryTableView
        historyTableView = UITableView(frame: CGRect(x: 0, y: lineView.bottom + kSizeFrom750_Landscape(32.0), width: view.width, height: jumpBtn.top - lineView.bottom - kSizeFrom750_Landscape(32.0)))
        historyTableView.dataSource = self
        historyTableView.delegate = self
        historyTableView.register(UITableViewCell.self, forCellReuseIdentifier: Identifier)
        self.view.addSubview(historyTableView)
        
        self.view.bringSubviewToFront(jumpBtn)
    }
}

// MARK: - UITableViewDataSource, UITableViewDelegate
extension H5ViewController: UITableViewDataSource, UITableViewDelegate {
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return historyArray?.count ?? 0
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: Identifier)
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: Identifier)
        }
        cell?.textLabel?.text =  historyArray?[indexPath.row]
        cell?.textLabel?.textColor = UIColor.init(0x333333, alphaValue: 1.0)
        cell?.textLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(30.0))
        cell?.imageView?.image = DKImage(named: "doraemon_search")
        return cell!
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        if self.historyArray?.count ?? 0 > indexPath.row {
            self.h5UrlTextView.text = self.historyArray![indexPath.row]
            self.jumpBtnClickHandle()
        }
    }
    
    func tableView(_ tableView: UITableView, heightForFooterInSection section: Int) -> CGFloat {
        return kSizeFrom750_Landscape(73.0)
    }
    
    func tableView(_ tableView: UITableView, viewForFooterInSection section: Int) -> UIView? {
        let footerView = UIView(frame: CGRect(x: 0, y: 0, width: view.width, height: kSizeFrom750_Landscape(73.0)))
        
        let clearButton = UIButton(frame: CGRect(x: (view.width - kSizeFrom750_Landscape(300.0)) / 2, y: kSizeFrom750_Landscape(40.0), width: kSizeFrom750_Landscape(300.0), height: 33.0))
        clearButton.setTitle("清除搜索历史", for: .normal)
        clearButton.setTitleColor(UIColor.init(0x999999), for: .normal)
        clearButton.titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(24.0))
        clearButton.addTarget(self, action: #selector(clearRecordBtnClickHandle(sender:)), for: .touchUpInside)
        footerView.addSubview(clearButton)
        
        return footerView
    }
    
    func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        return true
    }
    
    func tableView(_ tableView: UITableView, titleForDeleteConfirmationButtonForRowAt indexPath: IndexPath) -> String? {
        return LocalizedString("删除")
    }
    
    func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCell.EditingStyle, forRowAt indexPath: IndexPath) {
        CacheManager.shared.clearH5historicalRecord(text: self.historyArray![indexPath.row])
        self.historyArray = CacheManager.shared.h5historicalRecord
        self.historyTableView.reloadData()
    }
}
