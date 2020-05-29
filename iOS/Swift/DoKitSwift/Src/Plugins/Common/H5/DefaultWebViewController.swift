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
    private var _url : String?
    var url : String? {
        set {
            _url = newValue
            if verifyUrl(urlString: newValue) {
                let url = URL(string: newValue!)
                if let url = url {
                    let request = URLRequest(url: url)
                    self.webView.load(request)
                }
            } else {
                let loadErr = "ERROR: Start Page at '\(String(describing: newValue))' was not found."
                let html = "<html><body> \(loadErr) </body></html>"
                self.webView.loadHTMLString(html, baseURL: nil)
            }
        }
        get {
            return _url
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = LocalizedString("Doraemon内置浏览器")
        webView.frame = view.frame
        self.view.addSubview(webView)
    }
    
    func verifyUrl (urlString: String?) -> Bool {
        // Check for nil
        if let urlString = urlString {
            // create NSURL instance
            if let url = NSURL(string: urlString) {
                // check if your application can open the NSURL instance
                return UIApplication.shared.canOpenURL(url as URL)
            }
        }
        return false
    }
    
    override func observeValue(forKeyPath keyPath: String?, of object: Any?, change: [NSKeyValueChangeKey : Any]?, context: UnsafeMutableRawPointer?) {
        //  加载进度条
        if keyPath == "estimatedProgress" {
            progressView.alpha = 1.0
//            print(self.webView.estimatedProgress)
            progressView.setProgress(Float((self.webView.estimatedProgress)), animated: true)
            if (self.webView.estimatedProgress)  >= 1.0 {
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
//        self.webView.uiDelegate = nil
//        self.webView.navigationDelegate = nil
    }
    
    /// 添加进度条
    lazy private var progressView: UIProgressView = {
        self.progressView = UIProgressView.init(frame: CGRect(x: 0.0, y: kIphoneNavBarHeight, width: UIScreen.main.bounds.width, height: 1.0))
        self.progressView.tintColor = UIColor.blue()        // 进度条颜色
        self.progressView.trackTintColor = UIColor.white    // 进度条背景色
        return self.progressView
    }()
    
    lazy var webView: WKWebView = {
        let webView = WKWebView.init(frame: view.bounds)
        webView.scrollView.bounces = false
//        webView.uiDelegate = self
//        webView.navigationDelegate = self
        view.addSubview(webView)
        view.addSubview(self.progressView)
        view.bringSubviewToFront(self.progressView) // 将进度条至于最顶层
        webView.addObserver(self, forKeyPath: "estimatedProgress", options: .new, context: nil)
        return webView
    }()
}
