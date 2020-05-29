//
//  UIImageExtensions.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import Foundation

extension UIImage {
    
    convenience init?(_ named: String, of type: String = "png") {
        guard
            let url = Bundle(for: DoKit.self).url(forResource: "DoKitSwift", withExtension: "bundle"),
            let bundle = Bundle(url: url) else {
            return nil
        }
        guard let path = bundle.path(forResource: named.format(), ofType: type) else {
            return nil
        }
        self.init(contentsOfFile: path)
    }
    
    static func dynamic(with light: UIImage?, dark: UIImage?) -> UIImage? {
        if #available(iOS 13.0, *), UITraitCollection.current.userInterfaceStyle == .dark {
            return dark
            
        } else {
            return light
        }
    }
}

fileprivate extension String {
    
    func format(_ scale: CGFloat = UIScreen.main.scale) -> String {
        switch scale {
        case 2.0:   return "\(self)@2x"
        case 3.0:   return "\(self)@3x"
        default:    return self
        }
    }
}

#warning("老版本 待替换")
extension UIImage {
    
    static func dokitImageNamed(name: String) -> UIImage? {
        let bundle = Bundle(for: DoKit.self)
        let url = bundle.url(forResource: "DoKitSwift", withExtension: "bundle")
        if url == nil {
            return nil
        }
        let imageBundle = Bundle(url: url!)
        var imageName = name
        let scale = UIScreen.main.scale
        if abs(scale-3) <= 0.001{
            imageName = "\(name)@3x"
        }else if abs(scale-2) <= 0.001{
            imageName = "\(name)@2x"
        }
        var filePath = imageBundle?.path(forResource: imageName, ofType: "png")
        if let path = filePath {
            var image = UIImage(contentsOfFile: path)
            if image == nil {
                filePath = imageBundle?.path(forResource: name, ofType: "png")
                if let path = filePath {
                    image = UIImage(contentsOfFile: path)
                    if image == nil {
                        image = UIImage(named: name)
                        if image == nil {
                            return nil
                        }else{
                            return image
                        }
                    }else{
                        return image
                    }
                }
            }else{
                return image
            }
        }
        return nil
    }
}
