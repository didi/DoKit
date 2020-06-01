//
//  Backtrace.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/5/30.
//

import Foundation

public func backtrace(thread: Thread)->String{
    let name = "Backtrace of : \(thread.description)\n"
    if Thread.current == thread {
        return name +  Thread.callStackSymbols.joined(separator: "\n")
    }
    let mach = machThread(from: thread)
    return name + backtrace(t: mach)
}
public func backtraceMainThread()->String{
    return backtrace(thread: Thread.main)
}
public func backtraceCurrentThread()->String{
    return backtrace(thread: Thread.current)
}
public func backtraceAllThread()->[String]{
    var count: mach_msg_type_number_t = 0
    var threads: thread_act_array_t!
    guard task_threads(mach_task_self_, &(threads), &count) == KERN_SUCCESS else {
        return [backtrace(t: mach_thread_self())]
    }
    var symbols = [String]()
    for i in 0..<count {
        let prefix = "Backtrace of : thread \(i + 1)\n"
        symbols.append(prefix + backtrace(t: threads[Int(i)]))
    }
    return symbols
}

fileprivate func backtrace(t:thread_t)-> String{
    let maxSize: Int32 = 128
    let addrs = UnsafeMutablePointer<UnsafeMutableRawPointer?>.allocate(capacity: Int(maxSize))
    defer { addrs.deallocate() }
    let count = backtrace(t, stack: addrs, maxSize)
    var symbols: [String] = []
    if let bs = backtrace_symbols(addrs, count) {
        symbols = UnsafeBufferPointer(start: bs, count: Int(count)).map {
            guard let symbol = $0 else {
                return "<null>"
            }
            return String(cString: symbol)
        }
    }
    return symbols.joined(separator: "\n")
}

/// 声明C的符号
@_silgen_name("df_backtrace")
fileprivate func backtrace(_ thread: thread_t, stack: UnsafeMutablePointer<UnsafeMutableRawPointer?>!, _ maxSymbols: Int32) -> Int32

@_silgen_name("backtrace_symbols")
fileprivate func backtrace_symbols(_ stack: UnsafePointer<UnsafeMutableRawPointer?>!, _ frame: Int32) -> UnsafeMutablePointer<UnsafeMutablePointer<Int8>?>!


/**
    这里主要利用了Thread 和 pThread 共用一个Name的特性，找到对应 thread的内核线程thread_t
    但是主线程不行，主线程设置Name无效.
 */
public var main_thread_t: mach_port_t?
fileprivate func machThread(from thread: Thread) -> thread_t {
    var count: mach_msg_type_number_t = 0
    var threads: thread_act_array_t!
    
    guard task_threads(mach_task_self_, &(threads), &count) == KERN_SUCCESS else {
        return mach_thread_self()
    }

    /// 如果当前线程不是主线程，但是需要获取主线程的堆栈
    if !Thread.isMainThread && thread.isMainThread  && main_thread_t == nil {
        DispatchQueue.main.sync {
            main_thread_t = mach_thread_self()
        }
        return main_thread_t ?? mach_thread_self()
    }
    
    let originName = thread.name
    defer {
        thread.name = originName
    }
    let newName = String(Int(Date.init().timeIntervalSince1970))
    thread.name = newName
    for i in 0..<count {
        let machThread = threads[Int(i)]
        if let p_thread = pthread_from_mach_thread_np(machThread) {
            var name: [Int8] = Array<Int8>(repeating: 0, count: 128)
            pthread_getname_np(p_thread, &name, name.count)
            if thread.name == String(cString: name) {
                return machThread
            }
        }
    }
    return mach_thread_self()
}
