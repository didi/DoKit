//
//  CLLocationManager+Doraemon.m
//  DoraemonKit
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
        for(unsigned i = 0; i < count; i++)
        {
            SEL sel = methods[i].name;
            if ([delegate respondsToSelector:sel]) {
                if (![[DoraemonGPSMocker shareInstance] respondsToSelector:sel]) {
                    NSAssert(NO, @"Delegate : %@ not implementation SEL : %@",delegate,NSStringFromSelector(sel));

                }
            }
        }
        free(methods);
        
    }else{
        [self doraemon_swizzleLocationDelegate:delegate];
    }
}

@end
