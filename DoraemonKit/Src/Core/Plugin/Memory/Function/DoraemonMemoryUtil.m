//
//  DoraemonMemoryUtil.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/1/25.
//

#import "DoraemonMemoryUtil.h"
#include <mach/mach.h>

@implementation DoraemonMemoryUtil

+ (NSUInteger)useMemoryForApp{
    struct mach_task_basic_info info;
    mach_msg_type_number_t count = MACH_TASK_BASIC_INFO_COUNT;
    
    int r = task_info(mach_task_self(), MACH_TASK_BASIC_INFO, (task_info_t)& info, & count);
    if (r == KERN_SUCCESS)
    {
        return info.resident_size/1024/1024;
    }
    else
    {
        return -1;
    }
}

//设备总的内存
+ (NSUInteger)totalMemoryForDevice{
    return [NSProcessInfo processInfo].physicalMemory/1024/1024;
}

@end
