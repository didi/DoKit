//
//  MockFilterButton.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

class MockFilterButton: UIButton {
    
    override init(frame: CGRect) {
        super.init(frame: frame)
        setImage(DKImage(named: "doraemon_mock_filter_down"), for: .normal)
        setImage(DKImage(named: "doraemon_mock_filter_up"), for: .selected)
        setTitleColor(UIColor.black_1, for: .normal)
        adjustsImageWhenHighlighted = false
        titleLabel?.font = UIFont.systemFont(ofSize: kSizeFrom750_Landscape(28))
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    override func contentRect(forBounds bounds: CGRect) -> CGRect { bounds }
    
    override func titleRect(forContentRect contentRect: CGRect) -> CGRect {
        let titleSize = CGSize(width: 58, height: 18)
        let contentSize = contentRect.size
        return CGRect(x: (contentSize.width - titleSize.width)/2,
               y: (contentSize.height - titleSize.height)/2,
               width: titleSize.width,
               height: titleSize.height)
    }
    
    override func imageRect(forContentRect contentRect: CGRect) -> CGRect {
        let imageSide: CGFloat = 6
        let titleRect = self.titleRect(forContentRect: contentRect)
        let contentSize = contentRect.size
        return CGRect(x: titleRect.maxX + 4, y: (contentSize.height - imageSide)/2, width: imageSide, height: imageSide)
    }

}
