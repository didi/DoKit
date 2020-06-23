//
//  UIProfileViewController.swift
//  DoraemonKit-Swift
//
//  Created by jiaruh on 2020/6/23.
//

import UIKit

class UIProfileViewController: BaseViewController {
    
    lazy var switchView: CellSwitch = {
        let switchView = CellSwitch(frame: CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.width, height: kSizeFrom750(104)))
        switchView.renderUIWithTitle(title: LocalizedString("UI层级检查开关"),
                                     on: UIProfileManager.shared.isEnable)
        switchView.needTopLine()
        switchView.needDownLine()
        return switchView
    }()

    override func viewDidLoad() {
        super.viewDidLoad()
        configUI()
    }
    
    private func configUI() {
        self.setTitle(title: LocalizedString("UI层级"))
        self.view.addSubview(switchView)
        switchView.delegate = self
    }
    
    override func needBigTitleView() -> Bool {
        true
    }
    
}

extension UIProfileViewController: CellSwitchDelegate {
    func changeSwitchOn(on: Bool) {
        UIProfileManager.shared.isEnable = on
        HomeWindow.shared.hide()
    }
}
