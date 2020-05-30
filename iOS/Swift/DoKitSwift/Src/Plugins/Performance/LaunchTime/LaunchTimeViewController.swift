//
//  LaunchTimeViewController.swift
//  DoraemonKit-Swift
//
//  Created by objc on 2020/5/30.
//

import UIKit

class LaunchTimeViewController: BaseViewController {

    var cellBtn: CellButton!

    override func viewDidLoad() {
        super.viewDidLoad()

        // Do any additional setup after loading the view.
        self.setTitle(title: LocalizedString("启动耗时"))

        cellBtn = CellButton(frame: CGRect(x: 0, y: self.bigTitleView!.bottom, width: self.view.width, height: kSizeFrom750_Landscape(104)))
        let title = "\(LocalizedString("本次启动时间为")) :"
        let content = "\(LaunchTime.latency) s"
        cellBtn.renderUIWithTitle(title: title)
        cellBtn.renderUIWithRightContent(rightContent: content)
        cellBtn.needDownLine()
        view.addSubview(cellBtn)
    }

    override func needBigTitleView() -> Bool {
        return true
    }
}
