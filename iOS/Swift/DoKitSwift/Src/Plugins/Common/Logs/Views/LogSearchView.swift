//
//  LogSearchView.swift
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/20.
//

import UIKit
protocol LogSearchViewDelegate : NSObjectProtocol{
   func searchKeyword(keyword: String)
}

class LogSearchView: UIView, UITextFieldDelegate {
    weak var delegate:LogSearchViewDelegate?
    
    var searchIcon:UIImageView?
    var textFiled:UITextField?
    override init(frame: CGRect) {
        super.init(frame: frame)
        self.layer.borderWidth = 1.0;
        self.layer.cornerRadius = 8;
        self.layer.borderColor = UIColor.hexColor(0x999999, alphaValue: 0.2).cgColor
        
        searchIcon = UIImageView.init()
        searchIcon?.image = DKImage(named: "doraemon_search")
        searchIcon?.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(searchIcon!)
        self.addConstraint(NSLayoutConstraint.init(item: searchIcon!, attribute: .leading, relatedBy: .equal, toItem: self, attribute: .leading, multiplier: 1.0, constant: 10))
        self.addConstraint(NSLayoutConstraint.init(item: searchIcon!, attribute: .centerY, relatedBy: .equal, toItem: self, attribute: .centerY, multiplier: 1.0, constant: 0))
        self.addConstraint(NSLayoutConstraint.init(item: searchIcon!, attribute: .width, relatedBy: .equal, toItem: .none, attribute: .notAnAttribute, multiplier: 1.0, constant: 22))
        self.addConstraint(NSLayoutConstraint.init(item: searchIcon!, attribute: .height, relatedBy: .equal, toItem: .none, attribute: .notAnAttribute, multiplier: 1.0, constant: 22))

        textFiled = UITextField.init()
        textFiled?.delegate = self;
        textFiled?.placeholder = LocalizedString("请输入您要搜索的关键字")
        textFiled?.translatesAutoresizingMaskIntoConstraints = false
        self.addSubview(textFiled!)
        self.addConstraint(NSLayoutConstraint.init(item: textFiled!, attribute: .leading, relatedBy: .equal, toItem:searchIcon!, attribute: .trailing, multiplier: 1.0, constant: 10))
        self.addConstraint(NSLayoutConstraint.init(item: textFiled!, attribute: .top, relatedBy: .equal, toItem:self, attribute: .top, multiplier: 1.0, constant: 10))
        self.addConstraint(NSLayoutConstraint.init(item: textFiled!, attribute: .bottom, relatedBy: .equal, toItem:self, attribute: .bottom, multiplier: 1.0, constant: -10))
        self.addConstraint(NSLayoutConstraint.init(item: textFiled!, attribute: .trailing, relatedBy: .equal, toItem:self, attribute: .trailing, multiplier: 1.0, constant: -10))

    }
    
    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
    
    func textFieldDidChangeSelection(_ textField: UITextField) {
        let text = textField.text?.trimmingCharacters(in: .whitespaces)
        delegate?.searchKeyword(keyword: text!)
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.endEditing(true)
        return true
    }
}
