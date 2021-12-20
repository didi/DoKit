//
//  DoraemonWeakNetworkHandle.m
//  DoraemonKit
//
//  Created by didi on 2019/12/12.
//

#import "DoraemonWeakNetworkHandle.h"
#import "DoraemonWeakNetworkManager.h"
#import "DoraemonNetworkInterceptor.h"

@interface DoraemonWeakNetworkHandle()

@end

@implementation DoraemonWeakNetworkHandle

- (NSData *)weakFlow:(NSData *)data count:(NSInteger)times size:(NSInteger)weakSize{
    if(data.length < weakSize){
        return data;
    }
    NSRange range = NSMakeRange(weakSize * times, weakSize);
    NSInteger endPoint = weakSize * (times + 1);
    if(endPoint > data.length || ![DoraemonWeakNetworkManager shareInstance].shouldWeak){
        endPoint = data.length - weakSize * times;
        range = NSMakeRange(weakSize * times, endPoint);
    }
    return [data subdataWithRange:range];
}

@end
