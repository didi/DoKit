//
//  DefaultWebViewController.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit
import WebKit

class DefaultWebViewController: BaseViewController {
    private var url: String = "https://www.dokit.cn/"
    
    convenience init(url: String = "https://www.dokit.cn/") {
        self.init()
        self.url = url
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.view.backgroundColor = .white
        self.set(title: LocalizedString("Doraemon内置浏览器"))
        
        self.view.addSubview(self.webView)
        self.view.addSubview(self.progressView)
        self.view.bringSubviewToFront(self.progressView) // 将进度条至于最顶层
        self.webView.frame = CGRect(x: 0.0, y: kIphoneNavBarHeight, width: kScreenWidth, height: kScreenHeight - kIphoneNavBarHeight)
        self.webView.load(URLRequest.init(url: URL.init(string: self.url)!))
    }
            
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        //  加载进度条
        if keyPath == "estimatedProgress" {
            progressView.alpha = 1.0
//            print(self.webView.estimatedProgress)
            progressView.setProgress(Float((self.webView.estimatedProgress)), animated: true)
            if (self.webView.estimatedProgress) >= 1.0 {
                UIView.animate(withDuration: 0.3, delay: 0.1, options: .curveEaseOut, animations: {
                    self.progressView.alpha = 0
                }, completion: { (finish) in
                    self.progressView.setProgress(0.0, animated: false)
                })
            }
        }
    }
    
    deinit {
        self.webView.removeObserver(self, forKeyPath: "estimatedProgress")
    }
    
    /// 添加进度条
    private lazy var progressView: UIProgressView = {
        self.progressView = UIProgressView.init(frame: CGRect(x: 0.0, y: kIphoneNavBarHeight, width: kScreenWidth, height: 1.0))
        self.progressView.tintColor = .blue        // 进度条颜色
        self.progressView.trackTintColor = .white    // 进度条背景色
        return self.progressView
    }()
    
    private lazy var webView: WKWebView = {
        let webView = WKWebView.init(frame: view.bounds)
        webView.addObserver(self, forKeyPath: "estimatedProgress", options: .new, context: nil)
        return webView
    }()
}
