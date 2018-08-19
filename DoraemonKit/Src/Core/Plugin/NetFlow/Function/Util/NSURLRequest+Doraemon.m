//
//  NSURLRequest+Doraemon.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/4/11.
//

#import "NSURLRequest+Doraemon.h"
#import <objc/runtime.h>

@implementation NSURLRequest (Doraemon)

- (NSString *)requestId {
    return objc_getAssociatedObject(self, @"requestId");
}

- (void)setRequestId:(NSString *)requestId {
    objc_setAssociatedObject(self, @"requestId", requestId, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

- (NSNumber*)startTime {
    return objc_getAssociatedObject(self, @"startTime");
}

- (void)setStartTime:(NSNumber*)startTime {
    objc_setAssociatedObject(self, @"startTime", startTime, OBJC_ASSOCIATION_COPY_NONATOMIC);
}

@end
