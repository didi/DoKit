//
//  HealthViewController.swift
//  DoraemonKit-Swift
//
//  Created by Ailsa on 2020/6/15.
//

import UIKit

class HealthViewController: BaseViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.setTitle(title: LocalizedString("健康体检"))
    }
    
    override func needBigTitleView() -> Bool {
        return true
    }
}
