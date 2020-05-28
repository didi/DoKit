//
//  UIImage+DoKit.swift
//  AFNetworking
//
//  Created by didi on 2020/5/25.
//

import Foundation

extension UIImage{
    static func dokitImageNamed(name:String) -> UIImage? {
        let bundle = Bundle(for: DoKit.self)
        let url = bundle.url(forResource: "DoKitSwift", withExtension: "bundle")
        if url == nil {
            return nil
        }
        let imageBundle = Bundle(url: url!)
        var imageName = name
        let scale = UIScreen.main.scale
        if abs(scale-3)  <= 0.001{
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
