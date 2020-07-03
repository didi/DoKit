//
//  MemoryCalculator.swift
//  DoraemonKit-Swift
//
//  Created by hash0xd on 2020/6/24.
//

import Foundation
struct MemoryCalculator {
    static func memoryUsage() -> Double {
        var taskInfo = task_vm_info_data_t()
        var count = mach_msg_type_number_t(MemoryLayout<task_vm_info>.size) / 4
        let result: kern_return_t = withUnsafeMutablePointer(to: &taskInfo) {
            $0.withMemoryRebound(to: integer_t.self, capacity: 1) {
                task_info(mach_task_self_, task_flavor_t(TASK_VM_INFO), $0, &count)
            }
        }
        
        var used: UInt64 = 0
        if result == KERN_SUCCESS {
            used = UInt64(taskInfo.phys_footprint)
        }
        
        return Double(used) / 1024 / 1024
    }
    
    static var total: Double {
        return Double(ProcessInfo.processInfo.physicalMemory) / 1024 / 1024
    }
}
