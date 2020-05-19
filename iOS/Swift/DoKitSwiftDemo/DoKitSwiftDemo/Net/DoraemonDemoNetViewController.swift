//
//  DoraemonDemoNetViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/18.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit
import AFNetworking

class DoraemonDemoNetViewController: DoraemonDemoBaseViewController, UITableViewDelegate, UITableViewDataSource {
    var tableView: UITableView!
    var items: [String]! = [] {
        didSet {
            self.tableView.reloadData()
        }
    }
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("网络测试Dem")
        
        tableView = UITableView(frame: view.bounds)
        tableView.delegate = self
        tableView.dataSource = self
        view.addSubview(tableView)
        
        items  = ["发送一条URLConnection请求",
                 "发送一条NSURLSession请求",
                 "发送一条AFNetworking请求"]
    }
    
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return items.count
    }
    
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        var cell = tableView.dequeueReusableCell(withIdentifier: "NetCellId")
        if cell == nil {
            cell = UITableViewCell(style: .default, reuseIdentifier: "NetCellId")
        }
        cell?.textLabel?.text = DoraemonDemoLocalizedString(items[indexPath.row])
        
        return cell!;
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let row = indexPath.row
        switch row {
        case 0:
            self.netForURLConnection()
        case 1:
            self.netForNSURLSession()
        case 2:
            self.netForAFNetworking()
        default:
            return
        }
    }
    
    func netForURLConnection() {
        let url: URL = URL(string: "https://www.taobao.com/")!
        let request: URLRequest = URLRequest(url: url)
        NSURLConnection.sendAsynchronousRequest(request, queue: OperationQueue()) { (response:URLResponse?, data:Data?, error:Error?) in
            if error == nil {
                let result: String = String(data: data!, encoding: String.Encoding.utf8)!
                print("请求成功 = \(result)")
            }else{
                print("请求失败 error = \(error.debugDescription)")
            }
        }
    }
    
    func netForNSURLSession() {
        let url: URL = URL(string: "https://www.taobao.com/")!
        let request: URLRequest = URLRequest(url: url)
        let configuration: URLSessionConfiguration = URLSessionConfiguration.default
        let session:URLSession = URLSession(configuration: configuration)
        
        let task:URLSessionDataTask = session.dataTask(with: request) { (data:Data?, respanse:URLResponse?, error:Error?) in
            if error == nil{
                let result: String = String(data: data!, encoding: String.Encoding.utf8)!
                print("请求成功 = \(result)")
            }else{
                print("请求失败 error = \(error.debugDescription)")
            }
        }
        task.resume()
    }
    
    func netForAFNetworking() {
        let manager = AFHTTPSessionManager()
        manager.requestSerializer = AFHTTPRequestSerializer()
        manager.responseSerializer = AFHTTPResponseSerializer()
        manager.get("https://www.taobao.com/", parameters: nil, progress: nil, success: { (task, response) in
            let result: String = String(data: response! as! Data, encoding: String.Encoding.utf8)!
            print("请求成功 = \(result)")
        }) { (task, error) in
            print("error == \(error.localizedDescription)")
        }
    }
}
