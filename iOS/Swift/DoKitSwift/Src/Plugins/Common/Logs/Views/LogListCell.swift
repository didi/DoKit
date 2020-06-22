//
//  LogListCell.swift
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/19.
//

import UIKit

class LogListCell: UITableViewCell {
    static let identifier: String = "logListCellID"
    private lazy var arrowImage: UIImageView = {
        $0.image = DKImage(named: "doraemon_expand_no")
        $0.contentMode = .center
        $0.translatesAutoresizingMaskIntoConstraints = false
        return $0
    }(UIImageView())
    
    private lazy var logLabel: UILabel = {
        $0.translatesAutoresizingMaskIntoConstraints = false
        $0.textColor = .black
        $0.font = .systemFont(ofSize: 12)
        $0.numberOfLines = 0;
        return $0
    }(UILabel())

    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
   
    private func initUI() {
        translatesAutoresizingMaskIntoConstraints = false
        contentView.addSubview(arrowImage)
        contentView.addSubview(logLabel)
    
        contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage as Any, attribute: .leading, relatedBy: .equal, toItem: self.contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage, attribute: .top, relatedBy: .equal, toItem: self.contentView, attribute: .top, multiplier: 1.0, constant: 8))
        contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
        contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
                
        contentView.addConstraint(NSLayoutConstraint.init(item: logLabel, attribute: .leading, relatedBy: .equal, toItem: self.arrowImage, attribute: .trailing, multiplier: 1.0, constant: 5))
        contentView.addConstraint(NSLayoutConstraint.init(item: logLabel, attribute: .top, relatedBy: .equal, toItem: self.arrowImage, attribute: .top, multiplier: 1.0, constant: 0))
        contentView.addConstraint(NSLayoutConstraint.init(item: logLabel, attribute: .trailing, relatedBy: .equal, toItem: self.contentView, attribute: .trailing, multiplier: 1.0, constant: -15))
        contentView.addConstraint(NSLayoutConstraint.init(item: logLabel, attribute: .bottom, relatedBy: .equal, toItem: self.contentView, attribute: .bottom, multiplier: 1.0, constant: -8))
        
    }
    
    func renderWithModel(model:LogModel) {
        guard let content = model.content else {
            return
        }
        logLabel.text = "\(model.dateFormat)\n\(content)"
        let iconName = model.expand ? "doraemon_expand":"doraemon_expand_no"
        arrowImage.image = DKImage(named: iconName)
    }
}
