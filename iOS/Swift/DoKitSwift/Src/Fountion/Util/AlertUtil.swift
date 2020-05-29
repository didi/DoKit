//
//  DoKitAlertUtil.swift
//  AFNetworking
//
//  Created by didi on 2020/5/26.
//

import Foundation

class AlertUtil {
    static func handleAlertAction(vc: UIViewController, title: String, text: String, ok: String, cancel: String, okBlock:@escaping ()->Void, cancelBlock:@escaping ()->Void) {
        let alertController = UIAlertController(title: title, message: text, preferredStyle: .alert)
        let okAction = UIAlertAction(title: ok, style: .default) { (action: UIAlertAction) in
            okBlock()
        }
        let cancelAction = UIAlertAction(title: cancel, style: .cancel) { (action: UIAlertAction) in
            cancelBlock()
        }
        alertController.addAction(okAction)
        alertController.addAction(cancelAction)
        vc.present(alertController, animated: true, completion: nil)
    }
    
    static func handleAlertAction(vc: UIViewController?, text: String, okBlock:@escaping ()->Void) {
        let alertController = UIAlertController(title: LocalizedString("提示"), message: text, preferredStyle: .alert)
        let okAction = UIAlertAction(title: LocalizedString("确定"), style: .default, handler: { action in
            okBlock()
        })
        alertController.addAction(okAction)
        vc?.present(alertController, animated: true, completion: nil)
    }
}
