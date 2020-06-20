//
//  LogModel.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/18.
//

import UIKit

class LogModel: NSObject {
    var content:String?
    let logDate = Date()
    var cellHeight:CGFloat?
    var expand = false
    
    func calculationHeight(string:String,width:Int,font:UIFont) {
        let nsString : NSString = string as NSString
        let size = CGSize(width:width , height:1000)
        let attribute = NSDictionary(object:font, forKey:kCTFontAttributeName as! NSCopying)
        let resultSize = nsString.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: attribute as? [NSAttributedString.Key:Any], context: nil).size
        cellHeight = resultSize.height + 16 > 50 ? resultSize.height + 16 : 50
       }
}
