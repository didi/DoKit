//
//  Backtrace.swift
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/5/30.
//

import Foundation

public func backTrace(thread: Thread)->[String]{
    if thread.isMainThread{
        return backTraceMainThread()
    }
    if Thread.current == thread {
        return backTraceCurrentThread()
    }
    let mach = machThread(from: thread)
    return backTrace(t: mach)
}
public func backTraceMainThread()->[String]{
    if Thread.isMainThread{
        return Thread.callStackSymbols
    }
    return backTrace(thread: Thread.main)
}
public func backTraceCurrentThread()->[String]{
    return Thread.callStackSymbols
}
public func backTraceAllThread()->[[String]]{
    var count: mach_msg_type_number_t = 0
    var threads: thread_act_array_t!
    guard task_threads(mach_task_self_, &(threads), &count) == KERN_SUCCESS else {
        return [backTrace(t: mach_thread_self())]
    }
    var symbols = [[String]]()
    for i in 0..<count {
        symbols.append(backTrace(t: threads[Int(i)]))
    }
    return symbols
}

fileprivate func backTrace(t:thread_t)-> [String]{
    let maxSize: Int32 = 128
    let addrs = UnsafeMutablePointer<UnsafeMutableRawPointer?>.allocate(capacity: Int(maxSize))
    defer { addrs.deallocate() }
    let frameCount = backtrace(t, stack: addrs, maxSize)
    let bufferPointer = UnsafeBufferPointer(start: addrs, count: Int(frameCount))
    var symbols = [String]()
    for addr in bufferPointer {
        guard let addr = addr else { continue }
        let addrValue = UInt(bitPattern: addr)
        var info = dl_info()
        dladdr(UnsafeRawPointer(bitPattern: addrValue), &info)
        symbols.append(symbol(info: info))
    }
    return symbols
}

private func image(info: dl_info) -> String {
    if let dli_fname = info.dli_fname, let fname = String(validatingUTF8: dli_fname), let _ = fname.range(of: "/", options: .backwards, range: nil, locale: nil) {
        return (fname as NSString).lastPathComponent
    } else {
        return "???"
    }
}

/// returns: the symbol nearest the address
private func symbol(info: dl_info) -> String {
    if let dli_sname = info.dli_sname, let sname = String(validatingUTF8: dli_sname) {
        return sname
    } else if let dli_fname = info.dli_fname, let _ = String(validatingUTF8: dli_fname) {
        return image(info: info)
    } else {
        return String(format: "0x%1x", UInt(bitPattern: info.dli_saddr))
    }
}

@_silgen_name("dokit_backtrace")
fileprivate func backtrace(_ thread: thread_t, stack: UnsafeMutablePointer<UnsafeMutableRawPointer?>!, _ maxSymbols: Int32) -> Int32

//@_silgen_name("backtrace")
//fileprivate func backtrace(_ stack: UnsafeMutablePointer<UnsafeMutableRawPointer?>!, _ maxSymbols: Int32) -> Int32
//
//@_silgen_name("backtrace_symbols")
//fileprivate func backtrace_symbols(_ stack: UnsafePointer<UnsafeMutableRawPointer?>!, _ frame: Int32) -> UnsafeMutablePointer<UnsafeMutablePointer<Int8>?>!

//@_silgen_name("swift_demangle")
//fileprivate func _stdlib_demangleImpl(
//    mangledName: UnsafePointer<CChar>?,
//    mangledNameLength: UInt,
//    outputBuffer: UnsafeMutablePointer<CChar>?,
//    outputBufferSize: UnsafeMutablePointer<UInt>?,
//    flags: UInt32
//    ) -> UnsafeMutablePointer<CChar>?
//
//fileprivate func _stdlib_demangleName(_ mangledName: String) -> String {
//    return mangledName.utf8CString.withUnsafeBufferPointer {
//        (mangledNameUTF8CStr) in
//
//        let demangledNamePtr = _stdlib_demangleImpl(
//            mangledName: mangledNameUTF8CStr.baseAddress,
//            mangledNameLength: UInt(mangledNameUTF8CStr.count - 1),
//            outputBuffer: nil,
//            outputBufferSize: nil,
//            flags: 0)
//
//        if let demangledNamePtr = demangledNamePtr {
//            let demangledName = String(cString: demangledNamePtr)
//            free(demangledNamePtr)
//            return demangledName
//        }
//        return mangledName
//    }
//}


fileprivate func machThread(from thread: Thread) -> thread_t {
    var name: [Int8] = [Int8]()
    var count: mach_msg_type_number_t = 0
    var threads: thread_act_array_t!
    
    guard task_threads(mach_task_self_, &(threads), &count) == KERN_SUCCESS else {
        return mach_thread_self()
    }
    
    if thread.isMainThread {
        return mach_thread_self()
    }

    let originName = thread.name
    for i in 0..<count {
        let index = Int(i)
        if let p_thread = pthread_from_mach_thread_np((threads[index])) {
            name.append(Int8(Character.init("\0").ascii!))
            pthread_getname_np(p_thread, &name, MemoryLayout<Int8>.size * 256)
            if (strcmp(&name, (thread.name!.ascii)) == 0) {
                thread.name = originName
                return threads[index]
            }
        }
    }
    thread.name = originName
    return mach_thread_self()
}

extension Character {
    var isAscii: Bool {
        return unicodeScalars.allSatisfy { $0.isASCII }
    }
    var ascii: UInt32? {
        return isAscii ? unicodeScalars.first?.value : nil
    }
}

extension String {
    var ascii : [Int8] {
        var unicodeValues = [Int8]()
        for code in unicodeScalars {
            unicodeValues.append(Int8(code.value))
        }
        return unicodeValues
    }
}
