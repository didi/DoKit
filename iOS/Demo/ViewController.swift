//
//  ViewController.swift
//  Demo
//
//  Created by 唐佳诚 on 2021/9/22.
//

import UIKit
import DoraemonKit

class ViewController: UIViewController {
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view.
    }
    
    override func viewDidAppear(_ animated: Bool) {
        super.viewDidAppear(animated)
        var keyWindow: UIWindow? = nil
        if #available(iOS 13.0, *) {
            UIApplication.shared.connectedScenes.filter { scene in
                return scene.activationState == .foregroundActive
            }.forEach { scene in
                if let windowScene = scene as? UIWindowScene {
                    if #available(iOS 15.0, *) {
                        assert(keyWindow == nil, "keyWindow has been assigned")
                        keyWindow = windowScene.keyWindow
                    } else {
                        // Fallback on earlier versions
                        assert(false)
                    }
                }
            }
        } else {
            // Fallback on earlier versions
            keyWindow = UIApplication.shared.keyWindow
        }
        
        guard let keyWindow = keyWindow else {
            assert(false, "keyWindow not found")
            
            return
        }
        
        let touch = UITouch(point: CGPoint(x: 50, y: 50), window: keyWindow)
        let event = eventWithTouches([touch])
        guard let event = event else {
            assert(false, "eventWithTouches() return nil")
            
            return
        }
        UIApplication.shared.sendEvent(event)
        touch.dk_updateTimestamp(with: .ended)
        UIApplication.shared.sendEvent(event)
    }
    
    @IBAction private func buttonHandler(_ sender: Any) {
        print("buttonHandler")
    }
    
}
