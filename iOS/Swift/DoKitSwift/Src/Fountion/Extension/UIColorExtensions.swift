//
//  UIColor+DoKit.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import Foundation

extension UIColor {
    static func black_1() -> UIColor {
        return UIColor.hexColor(0x333333)
    }
    
    static func black_2() -> UIColor {
        return UIColor.hexColor(0x666666)
    }
    
    static func black_3() -> UIColor {
        return UIColor.hexColor(0x999999)
    }
    
    static func orange() -> UIColor{
        return UIColor.hexColor(0xFF8903)
    }
    
    static func bg() -> UIColor{
        return UIColor.hexColor(0xF4F5F6)
    }
    
    static func blue() -> UIColor{
        return UIColor.hexColor(0x337CC4)
    }
    
    static func line() -> UIColor{
        return UIColor.hexColor(0x000000, alphaValue: 0.1)
    }
    
    
    static func hexColor(_ hexValue: Int, alphaValue: Float) -> UIColor {
        return UIColor(red: CGFloat((hexValue & 0xFF0000) >> 16) / 255, green: CGFloat((hexValue & 0x00FF00) >> 8) / 255, blue: CGFloat(hexValue & 0x0000FF) / 255, alpha: CGFloat(alphaValue))
    }
    
    static func hexColor(_ hexValue: Int) -> UIColor {
        return hexColor(hexValue, alphaValue: 1)
    }
    
    convenience init(_ hexValue: Int, alphaValue: Float) {
        self.init(red: CGFloat((hexValue & 0xFF0000) >> 16) / 255, green: CGFloat((hexValue & 0x00FF00) >> 8) / 255, blue: CGFloat(hexValue & 0x0000FF) / 255, alpha: CGFloat(alphaValue))
    }
    
    convenience init(_ hexValue: Int) {
        self.init(hexValue, alphaValue: 1)
    }
    
    func toImage() -> UIImage {
        let rect = CGRect(x: 0, y: 0, width: 1, height: 1)
        UIGraphicsBeginImageContext(rect.size)
        let context = UIGraphicsGetCurrentContext()
        context?.setFillColor(self.cgColor)
        context?.fill(rect)
        let image = UIGraphicsGetImageFromCurrentImageContext()
        UIGraphicsEndImageContext()
        return image!
    }
    
    static func dynamic(with light: UIColor, dark: UIColor) -> UIColor {
        if #available(iOS 13.0, *) {
            return UIColor {
                switch $0.userInterfaceStyle {
                case .light:        return light
                case .dark:         return dark
                case .unspecified:  return light
                @unknown default:   return light
                }
            }
        } else {
            return light
        }
    }
}
