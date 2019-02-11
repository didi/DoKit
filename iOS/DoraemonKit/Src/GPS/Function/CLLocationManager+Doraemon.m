//
//  CLLocationManager+Doraemon.m
//  DoraemonKit-DoraemonKit
//
//  Created by yixiang on 2018/7/4.
//

#import "CLLocationManager+Doraemon.h"
#import "DoraemonGPSMocker.h"
#import <objc/runtime.h>

@implementation CLLocationManager (Doraemon)

- (void)doraemon_swizzleLocationDelegate:(id)delegate {
    if (delegate) {
        [self doraemon_swizzleLocationDelegate:[DoraemonGPSMocker shareInstance]];
        [[DoraemonGPSMocker shareInstance] addLocationBinder:self delegate:delegate];
        
        Protocol *proto = objc_getProtocol("CLLocationManagerDelegate");
        unsigned int count;
        struct objc_method_description *methods = protocol_copyMethodDescriptionList(proto, NO, YES, &count);
        NSMutableArray *array = [NSMutableArray array];
        for(unsigned i = 0; i < count; i++)
        {
            SEL sel = methods[i].name;
            if ([delegate respondsToSelector:sel]) {
                if (![[DoraemonGPSMocker shareInstance] respondsToSelector:sel]) {
                    NSAssert(NO, @"你在Delegate %@ 中所使用的SEL %@，暂不支持，请联系DoraemonKit开发者",delegate,sel);
                }
            }
        }
        free(methods);
        
    }else{
        [self doraemon_swizzleLocationDelegate:delegate];
    }
}

@end
