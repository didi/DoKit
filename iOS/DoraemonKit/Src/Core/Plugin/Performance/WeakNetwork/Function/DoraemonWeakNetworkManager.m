//
//  DoraemonWeakNetworkManager.m
//  AFNetworking
//
//  Created by didi on 2019/11/21.
//

#import "DoraemonWeakNetworkManager.h"
#import "DoraemonNetworkInterceptor.h"
#import "DoraemonWeakNetworkHandle.h"
#import "DoraemonDefine.h"

@interface DoraemonWeakNetworkManager()<DoraemonNetworkInterceptorDelegate,DoraemonNetworkWeakDelegate>

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
        instance.sleepTime = 1.0;
    });
    return instance;
}

- (void)canInterceptNetFlow:(BOOL)enable{
    _shouldWeak = enable;
    if (enable) {
        [DoraemonNetworkInterceptor.shareInstance addDelegate:self];
        [DoraemonNetworkInterceptor shareInstance].weakDelegate = self;
        _weakHandle = [[DoraemonWeakNetworkHandle alloc] init];
    }else{
        [DoraemonNetworkInterceptor.shareInstance removeDelegate:self];
        [DoraemonNetworkInterceptor shareInstance].weakDelegate = nil;
    }
}

- (BOOL)shouldWeak{
    return _shouldWeak;
}

- (BOOL)limitSpeed:(NSData *)data isDown:(BOOL)is{
    CGFloat speed = is ? _downFlowSpeed : _upFlowSpeed ;
    if(0 == data.length || data.length < (kbChange(speed) ? : kbChange(2000))){
        return true;
    }
    else{
        sleep(_sleepTime);
        return false;
    }
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
}

- (BOOL)shouldIntercept {
    return _shouldWeak;
}


#pragma mark - doraemonNSURLProtocolWeakNetDelegate
- (void)handleWeak:(NSData *)data isDown:(BOOL)is{
    NSUInteger count = 0;
    NSData *limitedData = nil;
    NSInteger speed = 0;
    speed = is ? _downFlowSpeed : _upFlowSpeed;
    while (true) {
        limitedData = [_weakHandle weakFlow:data count:count size:kbChange(speed) ? : kbChange(2000)];
        if([self limitSpeed:limitedData isDown:is]){
            return ;
        }
        //DoKitLog(@"count == %ld",count);
        count++;
    }
}

- (NSUInteger)delayTime{
    return _delayTime;
}

- (NSInteger)weakNetSelecte{
    return _selecte;
}

@end

