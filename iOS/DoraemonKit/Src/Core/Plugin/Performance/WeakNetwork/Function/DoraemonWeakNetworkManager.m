//
//  DoraemonWeakNetworkManager.m
//  AFNetworking
//
//  Created by didi on 2019/11/21.
//

#import "DoraemonWeakNetworkManager.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonWeakNetworkHandle.h"
#import <UIKit/UIKit.h>

@interface DoraemonWeakNetworkManager()<DoraemonNetworkInterceptorDelegate,DoraemonNetworkWeakDelegate>

@property (nonatomic, assign) NSUInteger selectItem;
@property (nonatomic, assign) NSUInteger weakSize;
@property (nonatomic, assign) CGFloat sleepTime;
@property (nonatomic, strong) DoraemonWeakNetworkHandle *weakHandle;

@end

@implementation DoraemonWeakNetworkManager

+ (DoraemonWeakNetworkManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonWeakNetworkManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonWeakNetworkManager alloc] init];
        instance.shouldWeak = NO;
        instance.weakSize = 1000;
    });
    return instance;
}

- (void)changeWeakSize:(NSInteger)size{
    if(size > 0){
        _shouldWeak = YES;
        _weakSize = size;
    }else{
        _shouldWeak = NO;
        _weakSize = 1000;
    }
}

- (void)canInterceptNetFlow:(BOOL)enable{
    _shouldWeak = enable;
    if (enable) {
        [DoraemonNetworkInterceptor.shareInstance addDelegate:self];
        [DoraemonNetworkInterceptor shareInstance].weakDelegate = self;
        _weakHandle = [[DoraemonWeakNetworkHandle alloc] init];
    }else{
        [DoraemonNetworkInterceptor.shareInstance removeDelegate:self];
    }
}

- (void)selectWeakItemChange:(NSUInteger)select sleepTime:(CGFloat)time weakSize:(NSUInteger)size{
    _selectItem = select;
    _weakSize = size;
    _sleepTime = time;
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    
}

- (BOOL)shouldIntercept {
    return _shouldWeak;
}

#pragma mark - doraemonNSURLProtocolWeakNetDelegate
- (NSData *)doraemonNSURLProtocolWeak:(NSData *)data count:(NSInteger)times{
    
    return [_weakHandle weakFlow:data count:times size:_weakSize];
}

- (BOOL)endWeak:(NSData *)data{
    
    if(0 == data.length || data.length < _weakSize){
        return true;
    }
    else{
        sleep(_sleepTime);
        return false;
    }
}

- (BOOL)shouldWeak{
    return _shouldWeak;
}


@end

