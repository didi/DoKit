//
//  UIImageExtensions.swift
//  DoraemonKit
//
//  Created by Lee on 2020/5/28.
//

import UIKit

func DKImage(named name: String, of type: String = "png") -> UIImage? {
    guard let bundle = sharedResourceBundle else {
        return nil
    }

    let onDarkMode: Bool
    if #available(iOS 13.0, *), UITraitCollection.current.userInterfaceStyle == .dark {
        onDarkMode = true
    } else {
        onDarkMode = false
    }

    let light = sortedScaleSuffix.map { return "\(name)\($0).\(type)" }
    let dark = sortedScaleSuffix.map { return "\(name)_dark\($0).\(type)" }

    if onDarkMode, let darkImage = DKImage(from: dark, in: bundle) {
        return darkImage
    } else {
        return DKImage(from: light, in: bundle)
    }
}

private let sharedResourceBundle: Bundle? = {
    let bundle = Bundle(for: DoKit.self)
    guard let url = bundle.url(forResource: "DoKitSwift", withExtension: "bundle"),
        let resourceBundle = Bundle(url: url) else {
        return nil
    }

    return resourceBundle
}()

private let sortedScaleSuffix: [String] = {
    let suffix: (string: String, index: Int)
    let scale = UIScreen.main.scale
    switch scale {
    case 3.0:   suffix = ("@3x", 0)
    case 2.0:   suffix = ("@2x", 1)
    default:    suffix = ("", 2)
    }

    var all: [String] = ["@3x", "@2x", ""]
    all.remove(at: suffix.index)
    all.insert(suffix.string, at: 0)
    return all
}()

private func DKImage(from names: [String], in bundle: Bundle) -> UIImage? {
    var result: UIImage? = nil
    for name in names {
        if let image = UIImage(named: name, in: bundle, compatibleWith: nil) {
            result = image
            break
        }
    }

    return result
}
