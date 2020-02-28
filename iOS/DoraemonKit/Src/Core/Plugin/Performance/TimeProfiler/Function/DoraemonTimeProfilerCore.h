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
} dtp_call_record;

extern void dtp_hook_begin(void);
extern void dtp_hook_end(void);

extern void dtp_set_min_time(uint64_t us); //default 1000
extern void dtp_set_max_depth(int depth); //deafult 10

extern dtp_call_record *dtp_get_call_records(int *num);
extern void dtp_clear_call_records(void);

#endif /* DoraemonTimeProfilerCore_h */
