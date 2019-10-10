//
//  DoraemonTimeProfilerCore.h
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2019/7/9.
//

#ifndef DoraemonTimeProfilerCore_h
#define DoraemonTimeProfilerCore_h

#include <stdio.h>
#include <objc/objc.h>

typedef struct {
    __unsafe_unretained Class cls;
    SEL sel;
    uint64_t time; // us （1/1000 ms）
    int depth;
} doraemon_call_record;

extern void doraemon_hook_begin();
extern void doraemon_hook_end();

extern void doraemon_set_min_time(uint64_t us); //default 1000
extern void doraemon_set_max_depth(int depth); //deafult 10

extern doraemon_call_record *doraemon_get_call_records(int *num);
extern void doraemon_clear_call_records();

#endif /* DoraemonTimeProfilerCore_h */
