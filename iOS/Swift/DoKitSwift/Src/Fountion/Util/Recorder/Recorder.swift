//
//  Recorder.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/6/3.
//

import Foundation

fileprivate extension String {
    var absoluteString: String {
        return NSHomeDirectory() + "/Library/Caches/Dokit/\(self)"
    }
}

public class Recorder {
    fileprivate let queue = DispatchQueue.init(label: "com.dokit.recorder")
    private init() {}
    
    static let `default` = Recorder()
    
    public func create(_ data:String,with fileName:String,into module:String) {
        queue.async {
            guard let data = data.data(using: .utf8) else{
                return
            }
            if !FileManager.default.fileExists(atPath: module.absoluteString){
                try? FileManager.default.createDirectory(atPath: module.absoluteString, withIntermediateDirectories: true, attributes: nil)
            }
            let path = module.absoluteString + "/" + fileName
            try? data.write(to: URL.init(fileURLWithPath: path), options: .atomic)
        }
    }
    
    public func append(_ data:String,to fileName:String,into module:String) {
        queue.async {
            let path = module.absoluteString + "/" + fileName
            if let fileHandle = FileHandle(forWritingAtPath: path),let data = data.data(using: .utf8) {
                defer {
                    fileHandle.closeFile()
                }
                fileHandle.seekToEndOfFile()
                fileHandle.write(data)
            }else {
                self.create(data, with: fileName,into: module)
            }
        }
    }
    
    public func deleteAll(in module:String) {
        queue.async {
            try? FileManager.default.removeItem(atPath: module.absoluteString)
        }
    }
    
    public func delete(fileName:String,in module:String) {
        queue.async {
            try? FileManager.default.removeItem(atPath: module.absoluteString + "/" + fileName)
        }
    }
    
    public func fileNames(completed:@escaping ([String])->Void,in module:String){
        queue.async {
            let files = try? FileManager.default.contentsOfDirectory(atPath: module.absoluteString)
            completed(files ?? [])
        }
    }
    
}
