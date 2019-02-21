//
//  NSURLSessionConfiguration+Doraemon.m
//  AFNetworking
//
//  Created by yixiang on 2018/7/2.
//

#import "NSURLSessionConfiguration+Doraemon.h"
#import "DoraemonNSURLProtocol.h"
#import "NSObject+Doraemon.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonCacheManager.h"


@implementation NSURLSessionConfiguration (Doraemon)

+ (void)load{
    [[self class] doraemon_swizzleClassMethodWithOriginSel:@selector(defaultSessionConfiguration) swizzledSel:@selector(doraemon_defaultSessionConfiguration)];
    [[self class] doraemon_swizzleClassMethodWithOriginSel:@selector(ephemeralSessionConfiguration) swizzledSel:@selector(doraemon_ephemeralSessionConfiguration)];
}

+ (NSURLSessionConfiguration *)doraemon_defaultSessionConfiguration{
    NSURLSessionConfiguration *configuration = [self doraemon_defaultSessionConfiguration];
    [DoraemonNetFlowManager setEnabled:YES forSessionConfiguration:configuration];
    return configuration;
}

+ (NSURLSessionConfiguration *)doraemon_ephemeralSessionConfiguration{
    NSURLSessionConfiguration *configuration = [self doraemon_ephemeralSessionConfiguration];
    [DoraemonNetFlowManager setEnabled:YES forSessionConfiguration:configuration];
    return configuration;
}

@end
