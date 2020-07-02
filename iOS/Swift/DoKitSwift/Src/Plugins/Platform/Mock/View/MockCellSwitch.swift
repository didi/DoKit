//
//  MockCellSwitch.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

class MockCellSwitch: CellSwitch {

    private var size = kSizeFrom750_Landscape(24)
    private var arrow = UIImageView(image: DKImage(named: "doraemon_mock_detail_up"))
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        backgroundColor = .white
        addSubview(arrow)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func layoutSubviews() {
        super.layoutSubviews()
        arrow.frame = .init(x: width - size*3, y: height/2-arrow.image!.size.height/2, width: arrow.image!.size.width, height: arrow.image!.size.height)
        switchView.frame = .init(x: arrow.left - switchView.width-size*2, y: height/2-switchView.height/2, width: switchView.width, height: switchView.height)
    }
    
    func setArrowDown(_ isDown: Bool) {
        let imageName = isDown ? "doraemon_mock_detail_down" : "doraemon_mock_detail_up"
        arrow.image = DKImage(named: imageName)
        setNeedsLayout()
    }
}
