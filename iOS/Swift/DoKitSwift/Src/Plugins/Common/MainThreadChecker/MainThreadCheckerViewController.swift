//
//  DoKitMainThreadCheckerViewController.swift
//  DoraemonKit
//
//  Created by 邓锋 on 2020/5/28.
//

import Foundation

class MainThreadCheckerViewController: BaseViewController, CellButtonDelegate {

    var cellBtn: CellButton!
    override func viewDidLoad() {
        super.viewDidLoad()
        self.setTitle(title: LocalizedString("子2线程UI"))
        
        cellBtn = CellButton(frame: CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.width, height: kSizeFrom750_Landscape(104)))
        cellBtn.renderUIWithTitle(title: LocalizedString("子线程UI"))
        cellBtn.renderUIWithRightContent(rightContent: "")
        cellBtn.delegate = self
        cellBtn.needDownLine()
        view.addSubview(cellBtn)
    }
    
    override func needBigTitleView() -> Bool {
        return true
    }
    
    func cellBtnClick() {
        
    }

}
