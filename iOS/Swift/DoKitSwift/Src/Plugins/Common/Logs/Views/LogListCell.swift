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
        arrowImage!.image = UIImage.init(named: "doraemon_expand_no");
        arrowImage?.translatesAutoresizingMaskIntoConstraints = false
        self.contentView.addSubview(arrowImage!)
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage as Any, attribute: .leading, relatedBy: .equal, toItem: self.contentView, attribute: .leading, multiplier: 1.0, constant: 15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .top, relatedBy: .equal, toItem: self.contentView, attribute: .top, multiplier: 1.0, constant: 15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .width, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: arrowImage!, attribute: .height, relatedBy: .equal, toItem: nil, attribute: .notAnAttribute, multiplier: 1.0, constant: 25))
        
        logLabel = UILabel.init()
        logLabel?.translatesAutoresizingMaskIntoConstraints = false
        logLabel?.textColor = .black
        logLabel?.font = .systemFont(ofSize: 12)
        self.contentView.addSubview(logLabel!)
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .leading, relatedBy: .equal, toItem: self.arrowImage, attribute: .trailing, multiplier: 1.0, constant: 15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .top, relatedBy: .equal, toItem: self.arrowImage, attribute: .top, multiplier: 1.0, constant: 0))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .trailing, relatedBy: .equal, toItem: self.contentView, attribute: .trailing, multiplier: 1.0, constant: -15))
        self.contentView.addConstraint(NSLayoutConstraint.init(item: logLabel!, attribute: .bottom, relatedBy: .equal, toItem: self.contentView, attribute: .bottom, multiplier: 1.0, constant: -15))
    }
    func renderWithModel(model:LogModel) {
        self.logLabel?.text = model.content
        model.cellHeight = self.cellHeight(string: model.content ?? "")
    }

    private func cellHeight(string:String) -> CGFloat{
        let nsString : NSString = string as NSString
        let size = CGSize(width: self.logLabel!.bounds.size.width , height:1000)
        let attribute = NSDictionary(object:self.logLabel!.font!, forKey:kCTFontAttributeName as! NSCopying)
        let resultSize = nsString.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: attribute as? [NSAttributedString.Key:Any], context: nil).size
        return resultSize.height
    }
}
