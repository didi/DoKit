
//
//  MockModel.swift
//  DoraemonKit-Swift
//
//  Created by 郝振壹 on 2020/6/29.
//

import Foundation

struct MockModel: Codable {
    var apiId: String
    var name: String
    var path: String
    var query: [String: String]
    var body: [String: String]
    var category: String
    var owner: String
    var editor: String
}


