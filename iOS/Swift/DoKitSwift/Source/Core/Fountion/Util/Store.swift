//
//  UserDefaults.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/5/29.
//

import Foundation

private let doKitStore = UserDefaults.init(suiteName: "com.dokit.store")

@propertyWrapper
struct Store<T> {
    
    let key: String
    let defaultValue: T
    
    init(_ key: String, defaultValue: T) {
        self.key = key
        self.defaultValue = defaultValue
    }
    
    var wrappedValue: T {
        get {
            return doKitStore?.object(forKey: key) as? T ?? defaultValue
        }
        set {
            doKitStore?.set(newValue, forKey: key)
            doKitStore?.synchronize()
        }
    }
}
