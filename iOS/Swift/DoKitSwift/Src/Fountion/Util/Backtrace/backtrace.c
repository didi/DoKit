//
//  backtrace.c
//  DoraemonKit-Swift
//
//  Created by 邓锋 on 2020/5/30.
//

#include "backtrace.h"
#include <stdio.h>
#include <stdlib.h>
#include <machine/_mcontext.h>

// macro `MACHINE_THREAD_STATE` shipped with system header is wrong..
#if defined __i386__
#define THREAD_STATE_FLAVOR x86_THREAD_STATE
#define THREAD_STATE_COUNT  x86_THREAD_STATE_COUNT
#define __framePointer      __ebp

#elif defined __x86_64__
#define THREAD_STATE_FLAVOR x86_THREAD_STATE64
#define THREAD_STATE_COUNT  x86_THREAD_STATE64_COUNT
#define __framePointer      __rbp

#elif defined __arm__
#define THREAD_STATE_FLAVOR ARM_THREAD_STATE
#define THREAD_STATE_COUNT  ARM_THREAD_STATE_COUNT
#define __framePointer      __r[7]

#elif defined __arm64__
#define THREAD_STATE_FLAVOR ARM_THREAD_STATE64
#define THREAD_STATE_COUNT  ARM_THREAD_STATE64_COUNT
#define __framePointer      __fp

#else
#error "Current CPU Architecture is not supported"
#endif

int dokit_backtrace(thread_t thread, void** stack, int maxSymbols) {
    _STRUCT_MCONTEXT machineContext;
    mach_msg_type_number_t stateCount = THREAD_STATE_COUNT;
    
    kern_return_t kret = thread_get_state(thread, THREAD_STATE_FLAVOR, (thread_state_t)&(machineContext.__ss), &stateCount);
    if (kret != KERN_SUCCESS) {
        return 0;
    }

    int i = 0;
#if defined(__arm__) || defined (__arm64__)
    stack[i] = (void *)machineContext.__ss.__lr;
    ++i;
#endif
    void **currentFramePointer = (void **)machineContext.__ss.__framePointer;
    while (i < maxSymbols) {
        void **previousFramePointer = *currentFramePointer;
        if (!previousFramePointer){
            break;
        }
        stack[i] = *(currentFramePointer+1);
        currentFramePointer = previousFramePointer;
        ++i;
    }
    return i;
}
