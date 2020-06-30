//
//  Network.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/30.
//

import Foundation

struct Network {
    typealias CompletionHandler = (Error?, [AnyHashable: Any]?)->Void
    
    static func get(url: String, param: [String: LosslessStringConvertible]?, completion: CompletionHandler?) {
        var urlString = url
        if let param = param {
            let paramString = param.reduce("?") { res, kv in
                let s = res + kv.0 + "=" + kv.1.description + "&"
                return s
            }
            urlString += paramString.prefix(paramString.count - 1)
        }
        
        guard let encodeUrl = urlString.addingPercentEncoding(withAllowedCharacters: .urlQueryAllowed), let reqUrl = URL(string: encodeUrl) else {
            fatalError("invalid url or param")
        }
        let session = URLSession(configuration: .default)
        let task = session.dataTask(with: reqUrl) { (data, response, error) in
            guard let handler = completion else { return }
            if let error = error {
                handler(error, nil)
            } else if let data = data {
                do {
                    let dict = try JSONSerialization.jsonObject(with: data, options: [.mutableContainers]) as? [String: Any]
                    handler(nil, dict)
                } catch {
                    handler(error, nil)
                }
            } else {
                handler(nil, nil)
            }
        }
        task.resume()
    }
}
