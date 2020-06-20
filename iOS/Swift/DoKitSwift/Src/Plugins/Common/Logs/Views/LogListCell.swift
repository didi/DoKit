//
//  LogListCell.swift
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/19.
//

import UIKit

class LogListCell: UITableViewCell {
    var arrowImage:UIImageView?
    var logLabel:UILabel?
    override init(style: UITableViewCell.CellStyle, reuseIdentifier: String?) {
        super.init(style: style, reuseIdentifier: reuseIdentifier)
        initUI()
    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
   
    private func initUI() {
        self.translatesAutoresizingMaskIntoConstraints = false
        arrowImage = UIImageView.init()
        arrowImage!.image = DKImage(named: "doraemon_expand_no")
        arrowImage?.contentMode = .center
        arrowImage?.translatesAutoresizingMaskIntoConstraints = false
        self.contentView.addSubview(arrowImage!)
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage as Any, attribute: .leading, relatedBy: .equal, toItem: self.contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .top, relatedBy: .equal, toItem: self.contentView, attribute: .top, multiplier: 1.0, constant: 8))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
        
        logLabel = UILabel.init()
        logLabel?.translatesAutoresizingMaskIntoConstraints = false
        logLabel?.textColor = .black
        logLabel?.font = .systemFont(ofSize: 12)
        logLabel?.numberOfLines = 0;
        self.contentView.addSubview(logLabel!)
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .leading, relatedBy: .equal, toItem: self.arrowImage, attribute: .trailing, multiplier: 1.0, constant: 5))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .top, relatedBy: .equal, toItem: self.arrowImage, attribute: .top, multiplier: 1.0, constant: 0))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .trailing, relatedBy: .equal, toItem: self.contentView, attribute: .trailing, multiplier: 1.0, constant: -15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .bottom, relatedBy: .equal, toItem: self.contentView, attribute: .bottom, multiplier: 1.0, constant: -8))
    }
    
    func renderWithModel(model:LogModel) {
        let formmat = "yyyy-MM-dd HH:mm:ss"
        let formmatter = DateFormatter()
        formmatter.dateFormat = formmat
        let dateString = formmatter.string(from: model.logDate)
        guard let content = model.content else {
            return
        }
        self.logLabel?.text = "\(dateString)\n\(content)"
        let iconName = model.expand ? "doraemon_expand":"doraemon_expand_no"
        arrowImage?.image = DKImage(named: iconName)
    }
}
