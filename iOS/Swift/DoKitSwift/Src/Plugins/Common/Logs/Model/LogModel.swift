//
//  LogModel.swift
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/18.
//

import UIKit

class LogModel: NSObject {
    
    let logDate = Date()
    var dateFormat:String = DoKitUtil.dateString(date:Date())
    var cellHeight:CGFloat?
    var expand = false
    
    var content:String? {
        didSet {
            cellHeight = calculationHeight(string: "\(dateFormat)\n\(content ?? "")", width: Int(kScreenWidth) - 53, font: UIFont.systemFont(ofSize: 12.0))
        }
    }

//    计算cell高度
    func calculationHeight(string:String,width:Int,font:UIFont) -> CGFloat {
        let nsString : NSString = string as NSString
        let size = CGSize(width:width , height:1000)
        let attribute = NSDictionary(object:font, forKey:kCTFontAttributeName as! NSCopying)
        let resultSize = nsString.boundingRect(with: size, options: .usesLineFragmentOrigin, attributes: attribute as? [NSAttributedString.Key:Any], context: nil).size
        return resultSize.height + 16 > 50 ? resultSize.height + 16 : 50
       }
    
}
