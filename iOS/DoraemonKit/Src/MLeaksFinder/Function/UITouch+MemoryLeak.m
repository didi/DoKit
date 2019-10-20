//
//  UITouch+MemoryLeak.m
//  MLeaksFinder
//
//  Created by 佘泽坡 on 8/31/16.
//  Copyright © 2016 zeposhe. All rights reserved.
//

#import "UITouch+MemoryLeak.h"
#import <objc/runtime.h>
#import "DoraemonCacheManager.h"

#if _INTERNAL_MLF_ENABLED

extern const void *const kLatestSenderKey;

@implementation UITouch (MemoryLeak)

+ (void)load {
    if ([[DoraemonCacheManager sharedInstance]  memoryLeak]){
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            [self swizzleSEL:@selector(setView:) withSEL:@selector(swizzled_setView:)];
        });
    }
}

- (void)swizzled_setView:(UIView *)view {
    [self swizzled_setView:view];
    
    if (view) {
        objc_setAssociatedObject([UIApplication sharedApplication],
                                 kLatestSenderKey,
                                 @((uintptr_t)view),
                                 OBJC_ASSOCIATION_RETAIN);
    }
}

@end

#endif
