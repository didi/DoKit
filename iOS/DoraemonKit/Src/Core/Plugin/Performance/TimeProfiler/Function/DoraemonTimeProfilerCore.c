//
//  DoraemonTimeProfilerCore.c
//  DoraemonKit
//
//  Created by yixiang on 2019/7/9.
//

#include "DoraemonTimeProfilerCore.h"

#ifdef __aarch64__
#include <pthread.h>
#include <stdlib.h>
#include <sys/time.h>
#include <objc/runtime.h>
#include <dispatch/dispatch.h>
#include "doraemon_fishhook.h"

static bool _call_record_enabled = true;
static uint64_t _min_time_cost = 1000; //us
static int _max_call_depth = 10;
static pthread_key_t _thread_key;
__unused static id (*orig_objc_msgSend)(id, SEL, ...);

static dtp_call_record *dtp_records;
static int dtp_record_num;
static int dtp_record_alloc;

typedef struct {
    id self; //通过 object_getClass 能够得到 Class 再通过 NSStringFromClass 能够得到类名
    Class cls;
    SEL cmd; //通过 NSStringFromSelector 方法能够得到方法名
    uint64_t time; //us
    uintptr_t lr; //link register
} thread_call_record;

typedef struct {
    thread_call_record *stack;
    int allocated_length;
    int index;
    bool is_main_thread;
} thread_call_stack;

static inline thread_call_stack *get_thread_call_stack() {
    thread_call_stack *cs = (thread_call_stack *)pthread_getspecific(_thread_key);//pthread_getpecific和pthread_setspecific实现同一个线程中不同函数间共享数据的一种很好的方式
    if (cs == NULL) {
        cs = (thread_call_stack *)malloc(sizeof(thread_call_stack));
        cs->stack = (thread_call_record *)calloc(128, sizeof(thread_call_record));
        cs->allocated_length = 64;
        cs->index = -1;
        cs->is_main_thread = pthread_main_np();
        pthread_setspecific(_thread_key, cs);
    }
    return cs;
}

static void release_thread_call_stack(void *ptr){
    thread_call_stack *cs = (thread_call_stack *)ptr;
    if (!cs) return;
    if (cs->stack) free(cs->stack);
    free(cs);
}

static inline void push_call_record(id _self, Class _cls, SEL _cmd, uintptr_t lr) {
    thread_call_stack *cs = get_thread_call_stack();
    if (cs) {
        int nextIndex = (++cs->index);
        if (nextIndex >= cs->allocated_length) {
            cs->allocated_length += 64;
            cs->stack = (thread_call_record *)realloc(cs->stack, cs->allocated_length * sizeof(thread_call_record));
        }
        thread_call_record *newRecord = &cs->stack[nextIndex];
        newRecord->self = _self;
        newRecord->cls = _cls;
        newRecord->cmd = _cmd;
        newRecord->lr = lr;
        if (cs->is_main_thread && _call_record_enabled) {
            struct timeval now;
            gettimeofday(&now, NULL);
            newRecord->time = (now.tv_sec % 100) * 1000000 + now.tv_usec;
        }
    }
}

static inline uintptr_t pop_call_record() {
    thread_call_stack *cs = get_thread_call_stack();
    int curIndex = cs->index;
    int nextIndex = cs->index--;
    thread_call_record *pRecord = &cs->stack[nextIndex];
    
    if (cs->is_main_thread && _call_record_enabled) {
        struct timeval now;
        gettimeofday(&now, NULL);
        uint64_t time = (now.tv_sec % 100) * 1000000 + now.tv_usec;
        if (time < pRecord->time) {
            time += 100 * 1000000;
        }
        uint64_t cost = time - pRecord->time;
        if (cost > _min_time_cost && cs->index < _max_call_depth) {
            if (!dtp_records) {
                dtp_record_alloc = 1024;
                dtp_records = malloc(sizeof(dtp_call_record) * dtp_record_alloc);
            }
            dtp_record_num++;
            if (dtp_record_num >= dtp_record_alloc) {
                dtp_record_alloc += 1024;
                dtp_records = realloc(dtp_records, sizeof(dtp_call_record) * dtp_record_alloc);
            }
            dtp_call_record *log = &dtp_records[dtp_record_num - 1];
            log->cls = pRecord->cls;
            log->depth = curIndex;
            log->sel = pRecord->cmd;
            log->time = cost;
        }
    }
    return pRecord->lr;
}

void before_objc_msgSend(id self, SEL _cmd, uintptr_t lr) {
    push_call_record(self, object_getClass(self), _cmd, lr);
}

uintptr_t after_objc_msgSend() {
    return pop_call_record();
}

// arm64 hook objc_msgSend
// volatile为可选关键字，表示不需要gcc对下面的汇编代码做任何优化

// 函数调用，value传入函数地址
// 将参数value(地址)传递给x12寄存器
// blr开始执行
#define call(b, value) \
    __asm volatile ("stp x8, x9, [sp, #-16]!\n"); \
    __asm volatile ("mov x12, %0\n" :: "r"(value)); \
    __asm volatile ("ldp x8, x9, [sp], #16\n"); \
    __asm volatile (#b " x12\n");

//保存寄存器参数信息
//依次将寄存器数据入栈
#define save() \
    __asm volatile ( \
        "stp x8, x9, [sp, #-16]!\n" \
        "stp x6, x7, [sp, #-16]!\n" \
        "stp x4, x5, [sp, #-16]!\n" \
        "stp x2, x3, [sp, #-16]!\n" \
        "stp x0, x1, [sp, #-16]!\n");

//还原寄存器参数信息
//依次将寄存器数据出栈
#define load() \
    __asm volatile ( \
        "ldp x0, x1, [sp], #16\n" \
        "ldp x2, x3, [sp], #16\n" \
        "ldp x4, x5, [sp], #16\n" \
        "ldp x6, x7, [sp], #16\n" \
        "ldp x8, x9, [sp], #16\n");

#define link(b, value) \
    __asm volatile ("stp x8, lr, [sp, #-16]!\n"); \
    __asm volatile ("sub sp, sp, #16\n"); \
    call(b, value); \
    __asm volatile ("add sp, sp, #16\n"); \
    __asm volatile ("ldp x8, lr, [sp], #16\n");

//程序执行完成,返回将继续执行lr中的函数
#define ret() __asm volatile ("ret\n");

/**
 *__naked__修饰的函数告诉编译器在函数调用的时候不使用栈保存参数信息，
 *同时函数返回地址会被保存到LR寄存器上。
 *由于msgSend本身就是用这个修饰符的，
 *因此在记录函数调用的出入栈操作中，
 *必须保证能够保存以及还原寄存器数据。
 *msgSend利用x0 - x9的寄存器存储参数信息，
 *可以手动使用sp寄存器来存储和还原这些参数信息
 */

//msgSend必须使用汇编实现
__attribute__((__naked__))
static void hook_objc_msgSend() {
    //before之前保存objc_msgSend的参数
    save()
    
    //将objc_msgSend执行的下一个函数地址传递给before_objc_msgSend的第二个参数x0 self, x1 _cmd, x2: lr address
    __asm volatile ("mov x2, lr\n");
    __asm volatile ("mov x3, x4\n");
    
    // 执行before_objc_msgSend   blr 除了从指定寄存器读取新的 PC 值外效果和 bl 一...
    call(blr, &before_objc_msgSend)
    
    // 恢复objc_msgSend参数，并执行
    load()
    call(blr, orig_objc_msgSend)
    
    //after之前保存objc_msgSend执行完成的参数
    save()
    
    //调用 after_objc_msgSend
    call(blr, &after_objc_msgSend)
    
    //将after_objc_msgSend返回的参数放入lr,恢复调用before_objc_msgSend前的lr地址
    // x0 是整数/指针args的第一个arg传递寄存器 x0 是整数/指针值的（第一个）返回值寄存器
    __asm volatile ("mov lr, x0\n");
    
    //恢复objc_msgSend执行完成的参数
    load()
    
    //方法结束,继续执行lr
    ret()
}


void dtp_hook_begin(void) {
    _call_record_enabled = true;
//    static dispatch_once_t onceToken;
//    dispatch_once(&onceToken, ^{
//        pthread_key_create(&_thread_key, &release_thread_call_stack);
//        rebind_symbols((struct rebinding[1]){"objc_msgSend", (void *)hook_objc_msgSend, (void **)&orig_objc_msgSend},1);
//    });
    pthread_key_create(&_thread_key, &release_thread_call_stack);
    doraemon_rebind_symbols((struct doraemon_rebinding[1]){"objc_msgSend", (void *)hook_objc_msgSend, (void **)&orig_objc_msgSend},1);
}

void dtp_hook_end(void) {
    _call_record_enabled = false;
    doraemon_rebind_symbols((struct doraemon_rebinding[1]){"objc_msgSend", (void *)orig_objc_msgSend, NULL},1);
}

void dtp_set_min_time(uint64_t us) {
    _min_time_cost = us;
}

void dtp_set_max_depth(int depth) {
    _max_call_depth = depth;
}

dtp_call_record *dtp_get_call_records(int *num) {
    if (num) {
        *num = dtp_record_num;
    }
    return dtp_records;
}

void dtp_clear_call_records(void) {
    if (dtp_records) {
        free(dtp_records);
        dtp_records = NULL;
    }
    dtp_record_num = 0;
}

#else

void dtp_hook_begin(void) {}

void dtp_hook_end(void) {}

void dtp_set_min_time(uint64_t us) {}

void dtp_set_max_depth(int depth) {}

dtp_call_record *dtp_get_call_records(int *num) {return NULL;}

void dtp_clear_call_records(void) {}

#endif
