//
//  FilePreviewController.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/6/23.
//

import Foundation
import WebKit

class FilePreviewController: BaseViewController {
    
    let filePath:URL
    init(filePath:String) {
        self.filePath = URL.init(fileURLWithPath: filePath)
        super.init(nibName: nil, bundle: nil)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    lazy var webView: WKWebView = WKWebView()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        setTitle(title: LocalizedString(filePath.lastPathComponent))
        setRightNavTitle(title: LocalizedString("导出"))
        
        self.view.addSubview(webView)
        webView.translatesAutoresizingMaskIntoConstraints = false
        webView.topAnchor.constraint(equalTo: view.topAnchor).isActive = true
        webView.bottomAnchor.constraint(equalTo: view.bottomAnchor).isActive = true
        webView.leftAnchor.constraint(equalTo: view.leftAnchor).isActive = true
        webView.rightAnchor.constraint(equalTo: view.rightAnchor).isActive = true
        
        webView.loadFileURL(filePath, allowingReadAccessTo: filePath)
    }
    
    override func rightNavTitleClick() {
        DoKitUtil.share(obj: filePath, from: self)
    }
}
