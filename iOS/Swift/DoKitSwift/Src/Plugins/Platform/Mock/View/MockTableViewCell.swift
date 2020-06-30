//
//  MockTableViewCell.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import UIKit

protocol MockTableViewCellModel {
    var title: String { get }
    var content: String { get }
    var isOpen: Bool { get set }
    var isFold: Bool { get set }
    var height: CGFloat { get }
}

extension MockTableViewCellModel {
    var height: CGFloat {
        if isFold { return kSizeFrom750_Landscape(104) }
        let height = kSizeFrom750_Landscape(128)
        return height
    }
}

class MockTableViewCell: UITableViewCell {

    private(set) var model: MockTableViewCellModel?
    
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        backgroundColor = .bg
        contentView.addSubViews(detailSwitch, infoLabel)
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func update(_ data: MockTableViewCellModel) {
        model = data
    }
    
    @objc private func expandClick() {
        model?.isFold.toggle()
    }

    private lazy var detailSwitch: MockCellSwitch = {
        $0.delegate = self
        $0.addGestureRecognizer(UITapGestureRecognizer(target: self, action: #selector(expandClick)))
        return $0
    }(MockCellSwitch())
    
    private lazy var infoLabel: UILabel = {
        $0.textColor = .black_1
        $0.font = .systemFont(ofSize: kSizeFrom750_Landscape(24))
        $0.numberOfLines = 0
        return $0
    }(UILabel())
}

extension MockTableViewCell: CellSwitchDelegate {
    
    func changeSwitchOn(on: Bool) {
        
    }
    
}
