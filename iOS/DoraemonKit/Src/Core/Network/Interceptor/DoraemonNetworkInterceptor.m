//
//  DoraemonNetworkInterceptor.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonNetworkInterceptor.h"
#import "DoraemonNSURLProtocol.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonResponseImageModel.h"

static DoraemonNetworkInterceptor *instance = nil;

@interface DoraemonNetworkInterceptor()

//@property (nonatomic, strong) NSMutableSet *listeners;
@property (nonatomic, strong) NSHashTable *delegates;
@end

@implementation DoraemonNetworkInterceptor

- (NSHashTable *)delegates {
    if (_delegates == nil) {
        self.delegates = [NSHashTable weakObjectsHashTable];
    }
    return _delegates;
}

- (void)addDelegate:(id<DoraemonNetworkInterceptorDelegate>) delegate {
    [self.delegates addObject:delegate];
    [self updateURLProtocolInterceptStatus];
}

- (void)removeDelegate:(id<DoraemonNetworkInterceptorDelegate>)delegate {
    [self.delegates removeObject:delegate];
    [self updateURLProtocolInterceptStatus];
}
    
//- (NSMutableSet *)listeners {
//    if (_listeners == nil) {
//        self.listeners = [NSMutableSet set];
//    }
//    return _listeners;
//}

+ (instancetype)shareInstance {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetworkInterceptor alloc] init];
    });
    return instance;
}

- (BOOL)shouldIntercept {
    // 当有对象监听 拦截后的网络请求时，才需要拦截
    BOOL shouldIntercept = NO;
    
    for (id<DoraemonNetworkInterceptorDelegate> delegate in self.delegates) {
        if (delegate.shouldIntercept) {
            shouldIntercept = YES;
        }
    }
    return shouldIntercept;
}

//- (void)addListeners:(id)listener {
//    [self.listeners addObject:listener];
//    [self updateURLProtocolInterceptStatus];
//}
//
//- (void)removeListeners:(id)listener {
//    [self.listeners removeObject:listener];
//    [self updateURLProtocolInterceptStatus];
//}

- (void)updateURLProtocolInterceptStatus {
    if (self.shouldIntercept) {
        [NSURLProtocol registerClass:[DoraemonNSURLProtocol class]];
    }else{
        [NSURLProtocol unregisterClass:[DoraemonNSURLProtocol class]];
    }
}

- (void)updateInterceptStatusForSessionConfiguration: (NSURLSessionConfiguration *)sessionConfiguration {
    //BOOL shouldIntercept = [self shouldIntercept];
    if ([sessionConfiguration respondsToSelector:@selector(protocolClasses)]
        && [sessionConfiguration respondsToSelector:@selector(setProtocolClasses:)]) {
        NSMutableArray * urlProtocolClasses = [NSMutableArray arrayWithArray: sessionConfiguration.protocolClasses];
        Class protoCls = DoraemonNSURLProtocol.class;
        if ( ![urlProtocolClasses containsObject: protoCls]) {
            [urlProtocolClasses insertObject: protoCls atIndex: 0];
        } else if ([urlProtocolClasses containsObject: protoCls]) {
            [urlProtocolClasses removeObject: protoCls];
        }
        sessionConfiguration.protocolClasses = urlProtocolClasses;
    }
}

- (void)handleResultWithData: (NSData *)data
                    response: (NSURLResponse *)response
                     request: (NSURLRequest *)request
                       error: (NSError *)error
                   startTime: (NSTimeInterval)startTime {
    for (id<DoraemonNetworkInterceptorDelegate> delegate in self.delegates) {
        [delegate doraemonNetworkInterceptorDidReceiveData:data response:response request:request error:error startTime:startTime];
    }
}

@end
