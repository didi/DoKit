//
//  QRCodeViewController.swift
//  DoraemonKit-Swift
//
//  Created by DeveloperLY on 2020/5/28.
//  
//

import UIKit

class QRCodeViewController: BaseViewController {
    private var scanView: QRScanView!
     
    var QRCodeBlock: ((_ result :String) -> Void)?
    
    
    override func viewDidLoad() {
        super.viewDidLoad()

        self.title = LocalizedString("扫描二维码")
        
    }
    
    override func viewWillAppear(_ animated: Bool) {
        super.viewWillAppear(animated)
        
        let scaner = QRScanView(frame: view.bounds)
        scaner.delegate = self
        scaner.isShowScanLine = true
        scaner.isShowBorderLine = true
        scaner.isShowCornerLine = true
        scaner.scanRect = CGRect(x: scaner.width / 2 - kSizeFrom750(480) / 2, y: kSizeFrom750(195), width: kSizeFrom750(480), height: kSizeFrom750(480))
        view.addSubview(scaner)
        scanView = scaner
        scaner.startScanning()
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        
        removeScanView()
    }
    
    func removeScanView() {
        if (scanView != nil) {
            scanView.stopScanning()
            scanView.removeFromSuperview()
            scanView = nil
        }
    }
}


extension QRCodeViewController: QRScanDelegate {
    func scanViewPickUpMessage(_ scanView: QRScanView, message: String) {
         if !message.isBlack {
           self.dismiss(animated: true) {
               if self.QRCodeBlock != nil {
                   self.QRCodeBlock!(message)
               }
           }
       }
    }

    func scanViewAroundBrightness(_ scanView: QRScanView, brightnessValue: String) {
        
    }
}
