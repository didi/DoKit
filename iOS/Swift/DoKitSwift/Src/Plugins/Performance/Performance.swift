//
//  Performance.swift
//  DoraemonKit-Swift
//
//  Created by objc on 2020/6/9.
//

import Foundation
import Darwin.Mach

private let TASK_BASIC_INFO_COUNT = mach_msg_type_number_t(MemoryLayout<task_basic_info_data_t>.size / MemoryLayout<UInt32>.size)
private let TASK_VM_INFO_COUNT = mach_msg_type_number_t(MemoryLayout<task_vm_info_data_t>.size / MemoryLayout<UInt32>.size)

struct Performance {
    static var cpuUsage: Double {
        var arr: thread_act_array_t?
        var threadCount: mach_msg_type_number_t = 0

        guard task_threads(mach_task_self_, &arr, &threadCount) == KERN_SUCCESS else { return -1 }
        guard let threads = arr else { return -1 }

        defer {
            let size = MemoryLayout<thread_t>.size * Int(threadCount)
            vm_deallocate(mach_task_self_, vm_address_t(bitPattern: threads), vm_size_t(size))
        }

        var total = 0.0

        for i in 0..<Int(threadCount) {
            var info = thread_basic_info()
            var infoCount = TASK_BASIC_INFO_COUNT

            let result = withUnsafeMutablePointer(to: &info) {
                $0.withMemoryRebound(to: integer_t.self, capacity: 1) {
                    thread_info(threads[i], thread_flavor_t(THREAD_BASIC_INFO), $0, &infoCount)
                }
            }
            guard result == KERN_SUCCESS else { return -1 }

            if info.flags & TH_FLAGS_IDLE == 0 {
                total += Double(info.cpu_usage) / Double(TH_USAGE_SCALE) * 100.0
            }
        }

        return total
    }

    static var memoryUsage: String {
        var info = task_vm_info_data_t()
        var infoCount = TASK_VM_INFO_COUNT

        let result = withUnsafeMutablePointer(to: &info) {
            $0.withMemoryRebound(to: integer_t.self, capacity: 1) {
                task_info(mach_task_self_, thread_flavor_t(TASK_VM_INFO), $0, &infoCount)
            }
        }
        guard result == KERN_SUCCESS else { return "" }

        return ByteCountFormatter.string(fromByteCount: Int64(info.internal), countStyle: .memory)
    }
}
