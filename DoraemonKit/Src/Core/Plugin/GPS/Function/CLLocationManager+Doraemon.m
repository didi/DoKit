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
        //1、让所有的CLLocationManager的代理都设置为[DoraemonGPSMocker shareInstance]，让他做中间转发
        [self doraemon_swizzleLocationDelegate:[DoraemonGPSMocker shareInstance]];
        //2、绑定所有CLLocationManager实例与delegate的关系，用于[DoraemonGPSMocker shareInstance]做目标转发用。
        [[DoraemonGPSMocker shareInstance] addLocationBinder:self delegate:delegate];
        
        //3、处理[DoraemonGPSMocker shareInstance]没有实现的selector，并且给用户提示。
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
