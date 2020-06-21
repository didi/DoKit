//
//  ANRDetailViewController.swift
//  DoraemonKit-Swift
//
//  Created by gengwenming on 2020/6/21.
//

import UIKit

class ANRDetailViewController: BaseViewController {
    
    var filePath = ""
    var anrInfo: [String: Any] = [:]

    override func viewDidLoad() {
        super.viewDidLoad()

        setTitle(title: LocalizedString("卡顿详情"))
        setRightNavTitle(title: LocalizedString("导出"))
        
        anrInfo = (NSDictionary(contentsOfFile: filePath) as? [String : Any]) ?? [:]
        
        let contentLabel = UILabel()
        contentLabel.textColor = .black_2()
        contentLabel.font = .systemFont(ofSize: kSizeFrom750_Landscape(16))
        contentLabel.numberOfLines = 0
        contentLabel.text = anrInfo["content"] as? String
        let fontSize = contentLabel.sizeThatFits(CGSize(width: kScreenWidth - 40, height: CGFloat(MAXFLOAT)))
        contentLabel.frame = CGRect(x: 20, y: kIphoneNavBarHeight + 10, width: fontSize.width, height: fontSize.height)
        
        let timeLabel = UILabel()
        timeLabel.textColor = .black_1()
        timeLabel.font = .systemFont(ofSize: kSizeFrom750_Landscape(16))
        let time = anrInfo["duration"] as? String ?? "0"
        timeLabel.text = "anr time : \(time)ms"
        timeLabel.sizeToFit()
        timeLabel.frame.origin = CGPoint(x: 20, y: contentLabel.bottom + 20)
        
        [contentLabel, timeLabel].forEach { view.addSubview($0) }
    }

    override func rightNavTitleClick() {
        DoKitUtil.share(obj: URL(fileURLWithPath: filePath), from: self)
    }
}
