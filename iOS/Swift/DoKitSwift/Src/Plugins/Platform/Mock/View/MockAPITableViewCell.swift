//
//  MockAPITableViewCell.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

protocol MockAPIScene {
    var title: String { get }
    var isSelected: Bool { get set }
}

protocol MockAPITableViewCellModel: MockTableViewCellModel {
    var scence: [MockAPIScene] { get set }
}

class MockAPITableViewCell: MockTableViewCell {

    override func update(_ data: MockTableViewCellModel) {
        guard let data = data as? MockAPITableViewCellModel else { return }
        print(data.content)
    }
    
    lazy var titleLabel: UILabel = {
        
        return $0
    }(UILabel())
}
