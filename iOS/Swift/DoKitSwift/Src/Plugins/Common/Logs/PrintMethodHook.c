//
//  PrintMethodHook.c
//  DoraemonKit-Swift
//
//  Created by orange on 2020/6/18.
//

#include "PrintMethodHook.h"
#include "fishhook.h"

///用于记录原始 fwrite 方法
extern void addLogFromSwift (char *);

static size_t (*orig_fwrite)(const void * __restrict __ptr, size_t __size, size_t __nitems, FILE * __restrict __stream);
size_t new_fwrite(const void * __restrict __ptr, size_t __size, size_t __nitems, FILE * __restrict __stream) {
    char *chars = (char*)__ptr;
    addLogFromSwift(chars);
    return orig_fwrite(__ptr, __size, __nitems, __stream);
}


static int  (*orin___swbuf)(int, FILE *);
int new___swbuf(int c, FILE *p) {
    char cChar = (char)c;
    
    return orin___swbuf(c, p);
}

void rebindPrintMethod (void) {
    int result = rebind_symbols((struct rebinding[1]){{"fwrite", new_fwrite, (void *)&orig_fwrite}}, 1);
    if (result < 0) {
        printf("fwrite绑定出错");
    }else {
        printf("fwrite绑定");
    }

    
}
void bindPrintMethod (void) {
    int result = rebind_symbols((struct rebinding[1]){{"fwrite", orig_fwrite, NULL}}, 1);
    if (result < 0) {
        printf("fwrite恢复出错");
    }else {
        printf("fwrite恢复");
    }
    
}




