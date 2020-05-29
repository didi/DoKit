//
//  UIApplication+MemoryLeak.m
//  MLeaksFinder
//
//  Created by 佘泽坡 on 5/11/16.
//  Copyright © 2016 zeposhe. All rights reserved.
//

#import "UIApplication+MemoryLeak.h"
#import "NSObject+MemoryLeak.h"
#import <objc/runtime.h>
#import "DoraemonCacheManager.h"

#if _INTERNAL_MLF_ENABLED

extern const void *const kLatestSenderKey;

@implementation UIApplication (MemoryLeak)

+ (void)load {
    if ([[DoraemonCacheManager sharedInstance] memoryLeak]){
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            [self swizzleSEL:@selector(sendAction:to:from:forEvent:) withSEL:@selector(swizzled_sendAction:to:from:forEvent:)];
        });
    }
}

- (BOOL)swizzled_sendAction:(SEL)action to:(id)target from:(id)sender forEvent:(UIEvent *)event {
    objc_setAssociatedObject(self, kLatestSenderKey, @((uintptr_t)sender), OBJC_ASSOCIATION_RETAIN);
    
    return [self swizzled_sendAction:action to:target from:sender forEvent:event];
}

@end

#endif
