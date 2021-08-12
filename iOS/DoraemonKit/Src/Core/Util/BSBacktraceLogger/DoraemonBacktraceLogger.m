//
//  DoraemonBacktraceLogger.m
//  DoraemonKit
//
//  Created by didi on 2020/3/18.
//

#import "DoraemonBacktraceLogger.h"
#import <mach/mach.h>
#include <dlfcn.h>
#include <pthread.h>
#include <sys/types.h>
#include <limits.h>
#include <string.h>
#include <mach-o/dyld.h>
#include <mach-o/nlist.h>

#pragma -mark DEFINE MACRO FOR DIFFERENT CPU ARCHITECTURE
#if defined(__arm64__)
#define Doraemon_DETAG_INSTRUCTION_ADDRESS(A) ((A) & ~(3UL))
#define Doraemon_THREAD_STATE_COUNT ARM_THREAD_STATE64_COUNT
#define Doraemon_THREAD_STATE ARM_THREAD_STATE64
#define Doraemon_FRAME_POINTER __fp
#define Doraemon_STACK_POINTER __sp
#define Doraemon_INSTRUCTION_ADDRESS __pc

#elif defined(__arm__)
#define Doraemon_DETAG_INSTRUCTION_ADDRESS(A) ((A) & ~(1UL))
#define Doraemon_THREAD_STATE_COUNT ARM_THREAD_STATE_COUNT
#define Doraemon_THREAD_STATE ARM_THREAD_STATE
#define Doraemon_FRAME_POINTER __r[7]
#define Doraemon_STACK_POINTER __sp
#define Doraemon_INSTRUCTION_ADDRESS __pc

#elif defined(__x86_64__)
#define Doraemon_DETAG_INSTRUCTION_ADDRESS(A) (A)
#define Doraemon_THREAD_STATE_COUNT x86_THREAD_STATE64_COUNT
#define Doraemon_THREAD_STATE x86_THREAD_STATE64
#define Doraemon_FRAME_POINTER __rbp
#define Doraemon_STACK_POINTER __rsp
#define Doraemon_INSTRUCTION_ADDRESS __rip

#elif defined(__i386__)
#define Doraemon_DETAG_INSTRUCTION_ADDRESS(A) (A)
#define Doraemon_THREAD_STATE_COUNT x86_THREAD_STATE32_COUNT
#define Doraemon_THREAD_STATE x86_THREAD_STATE32
#define Doraemon_FRAME_POINTER __ebp
#define Doraemon_STACK_POINTER __esp
#define Doraemon_INSTRUCTION_ADDRESS __eip

#endif

#define Doraemon_CALL_INSTRUCTION_FROM_RETURN_ADDRESS(A) (Doraemon_DETAG_INSTRUCTION_ADDRESS((A)) - 1)

#if defined(__LP64__)
#define Doraemon_TRACE_FMT         "%-4d%-31s 0x%016lx %s + %lu"
#define Doraemon_POINTER_FMT       "0x%016lx"
#define Doraemon_POINTER_SHORT_FMT "0x%lx"
#define Doraemon_NLIST struct nlist_64
#else
#define Doraemon_TRACE_FMT         "%-4d%-31s 0x%08lx %s + %lu"
#define Doraemon_POINTER_FMT       "0x%08lx"
#define Doraemon_POINTER_SHORT_FMT "0x%lx"
#define Doraemon_NLIST struct nlist
#endif

typedef struct DoraemonStackFrameEntry{
    const struct DoraemonStackFrameEntry *const previous;
    const uintptr_t return_address;
} DoraemonStackFrameEntry;

static mach_port_t main_thread_id;

@implementation DoraemonBacktraceLogger


+ (void)load {
    main_thread_id = mach_thread_self();
}

#pragma -mark Implementation of interface
+ (NSString *)doraemon_backtraceOfNSThread:(NSThread *)thread {
    return _doraemon_backtraceOfThread(doraemon_machThreadFromNSThread(thread));
}

+ (NSString *)doraemon_backtraceOfCurrentThread {
    return [self doraemon_backtraceOfNSThread:[NSThread currentThread]];
}

+ (NSString *)doraemon_backtraceOfMainThread {
    return [self doraemon_backtraceOfNSThread:[NSThread mainThread]];
}

+ (NSString *)doraemon_backtraceOfAllThread {
    thread_act_array_t threads;
    mach_msg_type_number_t thread_count = 0;
    const task_t this_task = mach_task_self();
    
    kern_return_t kr = task_threads(this_task, &threads, &thread_count);
    if(kr != KERN_SUCCESS) {
        return @"Fail to get information of all threads";
    }
    
    NSMutableString *resultString = [NSMutableString stringWithFormat:@"Call Backtrace of %u threads:\n", thread_count];
    for(int i = 0; i < thread_count; i++) {
        [resultString appendString:_doraemon_backtraceOfThread(threads[i])];
    }
    return [resultString copy];
}

#pragma -mark Get call backtrace of a mach_thread
NSString *_doraemon_backtraceOfThread(thread_t thread) {
    uintptr_t backtraceBuffer[50];
    int i = 0;
    NSMutableString *resultString = [[NSMutableString alloc] initWithFormat:@"Backtrace of Thread %u:\n", thread];
    
    _STRUCT_MCONTEXT machineContext;
    if(!doraemon_fillThreadStateIntoMachineContext(thread, &machineContext)) {
        return [NSString stringWithFormat:@"Fail to get information about thread: %u", thread];
    }
    
    const uintptr_t instructionAddress = doraemon_mach_instructionAddress(&machineContext);
    backtraceBuffer[i] = instructionAddress;
    ++i;
    
    uintptr_t linkRegister = doraemon_mach_linkRegister(&machineContext);
    if (linkRegister) {
        backtraceBuffer[i] = linkRegister;
        i++;
    }
    
    if(instructionAddress == 0) {
        return @"Fail to get instruction address";
    }
    
    DoraemonStackFrameEntry frame = {0};
    const uintptr_t framePtr = doraemon_mach_framePointer(&machineContext);
    if(framePtr == 0 ||
       doraemon_mach_copyMem((void *)framePtr, &frame, sizeof(frame)) != KERN_SUCCESS) {
        return @"Fail to get frame pointer";
    }
    
    for(; i < 50; i++) {
        backtraceBuffer[i] = frame.return_address;
        if(backtraceBuffer[i] == 0 ||
           frame.previous == 0 ||
           doraemon_mach_copyMem(frame.previous, &frame, sizeof(frame)) != KERN_SUCCESS) {
            break;
        }
    }
    
    int backtraceLength = i;
    Dl_info symbolicated[backtraceLength];
    doraemon_symbolicate(backtraceBuffer, symbolicated, backtraceLength, 0);
    for (int i = 0; i < backtraceLength; ++i) {
        [resultString appendFormat:@"%@", doraemon_logBacktraceEntry(i, backtraceBuffer[i], &symbolicated[i])];
    }
    [resultString appendFormat:@"\n"];
    return [resultString copy];
}

#pragma -mark Convert NSThread to Mach thread
thread_t doraemon_machThreadFromNSThread(NSThread *nsthread) {
    char name[256];
    mach_msg_type_number_t count;
    thread_act_array_t list;
    task_threads(mach_task_self(), &list, &count);
    
    NSTimeInterval currentTimestamp = [[NSDate date] timeIntervalSince1970];
    NSString *originName = [nsthread name];
    [nsthread setName:[NSString stringWithFormat:@"%f", currentTimestamp]];
    
    if ([nsthread isMainThread]) {
        return (thread_t)main_thread_id;
    }
    
    for (int i = 0; i < count; ++i) {
        pthread_t pt = pthread_from_mach_thread_np(list[i]);
        if ([nsthread isMainThread]) {
            if (list[i] == main_thread_id) {
                return list[i];
            }
        }
        if (pt) {
            name[0] = '\0';
            pthread_getname_np(pt, name, sizeof name);
            if (!strcmp(name, [nsthread name].UTF8String)) {
                [nsthread setName:originName];
                return list[i];
            }
        }
    }
    
    [nsthread setName:originName];
    return mach_thread_self();
}

#pragma -mark GenerateBacbsrackEnrty
NSString* doraemon_logBacktraceEntry(const int entryNum,
                               const uintptr_t address,
                               const Dl_info* const dlInfo) {
    char faddrBuff[20];
    char saddrBuff[20];
    
    const char* fname = doraemon_lastPathEntry(dlInfo->dli_fname);
    if(fname == NULL) {
        sprintf(faddrBuff, Doraemon_POINTER_FMT, (uintptr_t)dlInfo->dli_fbase);
        fname = faddrBuff;
    }
    
    uintptr_t offset = address - (uintptr_t)dlInfo->dli_saddr;
    const char* sname = dlInfo->dli_sname;
    if(sname == NULL) {
        sprintf(saddrBuff, Doraemon_POINTER_SHORT_FMT, (uintptr_t)dlInfo->dli_fbase);
        sname = saddrBuff;
        offset = address - (uintptr_t)dlInfo->dli_fbase;
    }
    return [NSString stringWithFormat:@"%-30s  0x%08" PRIxPTR " %s + %lu\n" ,fname, (uintptr_t)address, sname, offset];
}

const char* doraemon_lastPathEntry(const char* const path) {
    if(path == NULL) {
        return NULL;
    }
    
    char* lastFile = strrchr(path, '/');
    return lastFile == NULL ? path : lastFile + 1;
}

#pragma -mark HandleMachineContext
bool doraemon_fillThreadStateIntoMachineContext(thread_t thread, _STRUCT_MCONTEXT *machineContext) {
    mach_msg_type_number_t state_count = Doraemon_THREAD_STATE_COUNT;
    kern_return_t kr = thread_get_state(thread, Doraemon_THREAD_STATE, (thread_state_t)&machineContext->__ss, &state_count);
    return (kr == KERN_SUCCESS);
}

uintptr_t doraemon_mach_framePointer(mcontext_t const machineContext){
    return machineContext->__ss.Doraemon_FRAME_POINTER;
}

uintptr_t doraemon_mach_stackPointer(mcontext_t const machineContext){
    return machineContext->__ss.Doraemon_STACK_POINTER;
}

uintptr_t doraemon_mach_instructionAddress(mcontext_t const machineContext){
    return machineContext->__ss.Doraemon_INSTRUCTION_ADDRESS;
}

uintptr_t doraemon_mach_linkRegister(mcontext_t const machineContext){
#if defined(__i386__) || defined(__x86_64__)
    return 0;
#else
    return machineContext->__ss.__lr;
#endif
}

kern_return_t doraemon_mach_copyMem(const void *const src, void *const dst, const size_t numBytes){
    vm_size_t bytesCopied = 0;
    return vm_read_overwrite(mach_task_self(), (vm_address_t)src, (vm_size_t)numBytes, (vm_address_t)dst, &bytesCopied);
}

#pragma -mark Symbolicate
void doraemon_symbolicate(const uintptr_t* const backtraceBuffer,
                    Dl_info* const symbolsBuffer,
                    const int numEntries,
                    const int skippedEntries){
    int i = 0;
    
    if(!skippedEntries && i < numEntries) {
        doraemon_dladdr(backtraceBuffer[i], &symbolsBuffer[i]);
        i++;
    }
    
    for(; i < numEntries; i++) {
        doraemon_dladdr(Doraemon_CALL_INSTRUCTION_FROM_RETURN_ADDRESS(backtraceBuffer[i]), &symbolsBuffer[i]);
    }
}

bool doraemon_dladdr(const uintptr_t address, Dl_info* const info) {
    info->dli_fname = NULL;
    info->dli_fbase = NULL;
    info->dli_sname = NULL;
    info->dli_saddr = NULL;
    
    const uint32_t idx = doraemon_imageIndexContainingAddress(address);
    if(idx == UINT_MAX) {
        return false;
    }
    const struct mach_header* header = _dyld_get_image_header(idx);
    const uintptr_t imageVMAddrSlide = (uintptr_t)_dyld_get_image_vmaddr_slide(idx);
    const uintptr_t addressWithSlide = address - imageVMAddrSlide;
    const uintptr_t segmentBase = doraemon_segmentBaseOfImageIndex(idx) + imageVMAddrSlide;
    if(segmentBase == 0) {
        return false;
    }
    
    info->dli_fname = _dyld_get_image_name(idx);
    info->dli_fbase = (void*)header;
    
    // Find symbol tables and get whichever symbol is closest to the address.
    const Doraemon_NLIST* bestMatch = NULL;
    uintptr_t bestDistance = ULONG_MAX;
    uintptr_t cmdPtr = doraemon_firstCmdAfterHeader(header);
    if(cmdPtr == 0) {
        return false;
    }
    for(uint32_t iCmd = 0; iCmd < header->ncmds; iCmd++) {
        const struct load_command* loadCmd = (struct load_command*)cmdPtr;
        if(loadCmd->cmd == LC_SYMTAB) {
            const struct symtab_command* symtabCmd = (struct symtab_command*)cmdPtr;
            const Doraemon_NLIST* symbolTable = (Doraemon_NLIST*)(segmentBase + symtabCmd->symoff);
            const uintptr_t stringTable = segmentBase + symtabCmd->stroff;
            
            for(uint32_t iSym = 0; iSym < symtabCmd->nsyms; iSym++) {
                // If n_value is 0, the symbol refers to an external object.
                if(symbolTable[iSym].n_value != 0) {
                    uintptr_t symbolBase = symbolTable[iSym].n_value;
                    uintptr_t currentDistance = addressWithSlide - symbolBase;
                    if((addressWithSlide >= symbolBase) &&
                       (currentDistance <= bestDistance)) {
                        bestMatch = symbolTable + iSym;
                        bestDistance = currentDistance;
                    }
                }
            }
            if(bestMatch != NULL) {
                info->dli_saddr = (void*)(bestMatch->n_value + imageVMAddrSlide);
                info->dli_sname = (char*)((intptr_t)stringTable + (intptr_t)bestMatch->n_un.n_strx);
                if(*info->dli_sname == '_') {
                    info->dli_sname++;
                }
                // This happens if all symbols have been stripped.
                if(info->dli_saddr == info->dli_fbase && bestMatch->n_type == 3) {
                    info->dli_sname = NULL;
                }
                break;
            }
        }
        cmdPtr += loadCmd->cmdsize;
    }
    return true;
}

uintptr_t doraemon_firstCmdAfterHeader(const struct mach_header* const header) {
    switch(header->magic) {
        case MH_MAGIC:
        case MH_CIGAM:
            return (uintptr_t)(header + 1);
        case MH_MAGIC_64:
        case MH_CIGAM_64:
            return (uintptr_t)(((struct mach_header_64*)header) + 1);
        default:
            return 0;  // Header is corrupt
    }
}

uint32_t doraemon_imageIndexContainingAddress(const uintptr_t address) {
    const uint32_t imageCount = _dyld_image_count();
    const struct mach_header* header = 0;
    
    for(uint32_t iImg = 0; iImg < imageCount; iImg++) {
        header = _dyld_get_image_header(iImg);
        if(header != NULL) {
            // Look for a segment command with this address within its range.
            uintptr_t addressWSlide = address - (uintptr_t)_dyld_get_image_vmaddr_slide(iImg);
            uintptr_t cmdPtr = doraemon_firstCmdAfterHeader(header);
            if(cmdPtr == 0) {
                continue;
            }
            for(uint32_t iCmd = 0; iCmd < header->ncmds; iCmd++) {
                const struct load_command* loadCmd = (struct load_command*)cmdPtr;
                if(loadCmd->cmd == LC_SEGMENT) {
                    const struct segment_command* segCmd = (struct segment_command*)cmdPtr;
                    if(addressWSlide >= segCmd->vmaddr &&
                       addressWSlide < segCmd->vmaddr + segCmd->vmsize) {
                        return iImg;
                    }
                }
                else if(loadCmd->cmd == LC_SEGMENT_64) {
                    const struct segment_command_64* segCmd = (struct segment_command_64*)cmdPtr;
                    if(addressWSlide >= segCmd->vmaddr &&
                       addressWSlide < segCmd->vmaddr + segCmd->vmsize) {
                        return iImg;
                    }
                }
                cmdPtr += loadCmd->cmdsize;
            }
        }
    }
    return UINT_MAX;
}

uintptr_t doraemon_segmentBaseOfImageIndex(const uint32_t idx) {
    const struct mach_header* header = _dyld_get_image_header(idx);
    
    // Look for a segment command and return the file image address.
    uintptr_t cmdPtr = doraemon_firstCmdAfterHeader(header);
    if(cmdPtr == 0) {
        return 0;
    }
    for(uint32_t i = 0;i < header->ncmds; i++) {
        const struct load_command* loadCmd = (struct load_command*)cmdPtr;
        if(loadCmd->cmd == LC_SEGMENT) {
            const struct segment_command* segmentCmd = (struct segment_command*)cmdPtr;
            if(strcmp(segmentCmd->segname, SEG_LINKEDIT) == 0) {
                return segmentCmd->vmaddr - segmentCmd->fileoff;
            }
        }
        else if(loadCmd->cmd == LC_SEGMENT_64) {
            const struct segment_command_64* segmentCmd = (struct segment_command_64*)cmdPtr;
            if(strcmp(segmentCmd->segname, SEG_LINKEDIT) == 0) {
                return (uintptr_t)(segmentCmd->vmaddr - segmentCmd->fileoff);
            }
        }
        cmdPtr += loadCmd->cmdsize;
    }
    return 0;
}

@end
