//
//  backtrace.h
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/5/30.
//

#ifndef backtrace_h
#define backtrace_h

#include <mach/mach.h>

int dokit_backtrace(thread_t thread, void** stack, int maxSymbols);

#endif /* backtrace_h */
