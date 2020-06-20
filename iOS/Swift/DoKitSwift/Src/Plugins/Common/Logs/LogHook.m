//
//  LogHook.m
//  DoraemonKit-Swift
//
//  Created by I am Groot on 2020/6/15.
//

#import "LogHook.h"
#import <fishhook/fishhook.h>
@implementation LogHook
+ (instancetype)shared {
    static LogHook *instance;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[super allocWithZone:NULL] init];
    });
    return instance;
}
+ (instancetype)allocWithZone:(struct _NSZone *)zone {
    return [LogHook shared];
}
- (id)copyWithZone:(nullable NSZone *)zone {
    return [LogHook shared];
}
- (id)mutableCopyWithZone:(nullable NSZone *)zone {
    return [LogHook shared];
}
@end


static char *__chineseChar = {0};
static int __buffIdx = 0;
static NSString *__syncToken = @"token";
static size_t (*orig_fwrite)(const void * __restrict, size_t, size_t, FILE * __restrict);

size_t new_fwrite(const void * __restrict ptr, size_t size, size_t nitems, FILE * __restrict stream) {
    char *str = (char *)ptr;
    __block NSString *s = [NSString stringWithCString:str encoding:NSUTF8StringEncoding];
    dispatch_async(dispatch_get_main_queue(), ^{
        @synchronized (__syncToken) {
            if (__chineseChar != NULL) {
                if (str[0] == '\n' && __chineseChar[0] != '\0') {
                    s = [[NSString stringWithCString:__chineseChar encoding:NSUTF8StringEncoding] stringByAppendingString:s];
                    __buffIdx = 0;
                    __chineseChar = calloc(1, sizeof(char));
                }
            } else {
                
            }
        }
    });
    return orig_fwrite(ptr, size, nitems, stream);
}

static int (*orin___swbuf)(int, FILE *);
static int new___swbuf(int c, FILE *p) {
    @synchronized (__syncToken) {
        __chineseChar = realloc(__chineseChar, sizeof(char) * (__buffIdx + 2));
        __chineseChar[__buffIdx] = (char)c;
        __chineseChar[__buffIdx + 1] = '\0';
        __buffIdx++;
    }
    return orin___swbuf(c, p);
}

// 发现新问题, 这个方法和NSLog重复了.. 所以把不hook NSLog了
static ssize_t (*orig_writev)(int, const struct iovec *, int);
static ssize_t new_writev(int a, const struct iovec *v, int v_len) {
    NSMutableString *string = [NSMutableString string];
    for (int i = 0; i < v_len; i++) {
        char *c = (char *)v[i].iov_base;
        [string appendString:[NSString stringWithCString:c encoding:NSUTF8StringEncoding]];
    }
    ssize_t result = orig_writev(a, v, v_len);
    dispatch_async(dispatch_get_main_queue(), ^{
//        NSLog(@"string:%@",string);
//        [[logInWindowManager share] addPrintWithMessage:string needReturn:false];
    });
    return result;
}

void rebindFunction(void) {
    int error = 0;
//    error = rebind_symbols((struct rebinding[1]){{"writev", new_writev, (void *)&orig_writev}}, 1);
//    if (error < 0) {
//        NSLog(@"错误 writev");
//    }
    error = rebind_symbols((struct rebinding[1]){{"fwrite", new_fwrite, (void *)&orig_fwrite}}, 1);
    if (error < 0) {
        NSLog(@"错误 fwrite");
    }else {
        NSLog(@"绑定成功");
    }
    error = rebind_symbols((struct rebinding[1]){{"__swbuf", new___swbuf, (void *)&orin___swbuf}}, 1);
    if (error < 0) {
        NSLog(@"错误 __swbuf");
    }
}
void bindFuntion(void) {
    int error = 0;
    error = rebind_symbols((struct rebinding[1]){{"fwrite", orig_fwrite, NULL}}, 1);
    if (error < 0) {
        NSLog(@"错误 fwrite");
    }else {
        NSLog(@"解绑成功");
    }
    
    error = rebind_symbols((struct rebinding[1]){{"__swbuf", orin___swbuf, NULL}}, 1);
    if (error < 0) {
        NSLog(@"错误 __swbuf");
    }
    
}
