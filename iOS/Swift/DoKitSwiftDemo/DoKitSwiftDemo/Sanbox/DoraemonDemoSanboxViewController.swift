//
//  DoraemonDemoSanboxViewController.swift
//  DoKitSwiftDemo
//
//  Created by didi on 2020/5/13.
//  Copyright © 2020 didi. All rights reserved.
//

import UIKit

class DoraemonDemoSanboxViewController: DoraemonDemoBaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = DoraemonDemoLocalizedString("沙盒测试Demo");
        
        let btn = UIButton(frame: CGRect(x: 0, y: kIphoneNavBarHeight, width: view.width, height: 60.0))
        btn.backgroundColor = UIColor.orange
        btn.setTitle(DoraemonDemoLocalizedString("添加一条json到沙盒中"), for: .normal)
        btn.addTarget(self, action: #selector(addFile), for: .touchUpInside);
        view.addSubview(btn)
        
        let btn1 = UIButton(frame: CGRect(x: 0, y: btn.bottom+20, width: btn.width, height: 60))
        btn1.backgroundColor = UIColor.orange
        btn1.setTitle(DoraemonDemoLocalizedString("添加一张图片到沙盒中"), for: .normal)
        btn1.addTarget(self, action: #selector(addImageFile), for: .touchUpInside);
        view.addSubview(btn1)
        
        let btn2 = UIButton(frame: CGRect(x: 0, y: btn1.bottom+20, width: btn.width, height: 60))
        btn2.backgroundColor = UIColor.orange
        btn2.setTitle(DoraemonDemoLocalizedString("添加一段mp4到沙盒中"), for: .normal)
        btn2.addTarget(self, action: #selector(addMP4File), for: .touchUpInside);
        view.addSubview(btn2)
        
        let btn3 = UIButton(frame: CGRect(x: 0, y: btn2.bottom+20, width: btn.width, height: 60))
        btn3.backgroundColor = UIColor.orange
        btn3.setTitle(DoraemonDemoLocalizedString("添加doc、xlsx、pdf到沙盒中"), for: .normal)
        btn3.addTarget(self, action: #selector(addOtherFile), for: .touchUpInside);
        view.addSubview(btn3)
        
        let btn4 = UIButton(frame: CGRect(x: 0, y: btn3.bottom+20, width: btn.width, height: 60))
        btn4.backgroundColor = UIColor.orange
        btn4.setTitle(DoraemonDemoLocalizedString("添加html到沙盒中"), for: .normal)
        btn4.addTarget(self, action: #selector(addHtmlFile), for: .touchUpInside);
        view.addSubview(btn4)
    }
    
    @objc func addFile() {
        let dic = ["name":"yixiang","age":"16"]
        let json = dic.formatJson()
        if let json = json {
            let docDir = NSSearchPathForDirectoriesInDomains(FileManager.SearchPathDirectory.documentDirectory,  FileManager.SearchPathDomainMask.userDomainMask, true)[0]
            let filePath = docDir+"/json.text"
            try? json.write(toFile: filePath, atomically: true, encoding: String.Encoding.utf8)
        }
    }
    
    @objc func addImageFile() {
        let homeDir = NSHomeDirectory()
        let docDic = homeDir + "/Documents"
        let imagePath = docDic + "/zhaoliyin.jpg"
        let image = UIImage(named: "zhaoliyin.jpg")
        let imageData = image!.pngData()
        try? imageData?.write(to: URL(fileURLWithPath: imagePath))
    }
    
    @objc func addMP4File() {
        copyBundleToSanbox("huoying", "mp4")
    }
    
    @objc func addOtherFile() {
        copyBundleToSanbox("Doraemon", "docx")
        copyBundleToSanbox("Doraemon", "pdf")
        copyBundleToSanbox("Doraemon", "xlsx")
    }
    
    @objc func addHtmlFile() {
        copyBundleToSanbox("doraemon", "html")
    }

    func copyBundleToSanbox(_ name: String, _ type: String) {
        let path = Bundle.main.path(forResource: name, ofType: type)
        let fileManager = FileManager.default
        let fileExists = fileManager.fileExists(atPath: path!)
        if fileExists {
            let homeDir = NSHomeDirectory()
            let docDic = homeDir + "/Documents"
            let toPath = docDic + "/\(name).\(type)"
            if !fileManager.fileExists(atPath: toPath) {
                do {
                    try fileManager.copyItem(atPath: path!, toPath: toPath)
                }catch{
                    print("catch error")
                }
            }
        }else{
            print("file not exist")
        }
    }
}
