//
//  DoraemonMCNetInterceptor.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonMultiNetworkInterceptor.h"
#import "DoraemonMultiURLProtocol.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonResponseImageModel.h"
#import "DoraemonDefine.h"

static DoraemonMultiNetworkInterceptor *instance = nil;

@interface DoraemonMultiNetworkInterceptor()


@property (nonatomic, strong) NSHashTable *delegates;

@end

@implementation DoraemonMultiNetworkInterceptor

- (NSHashTable *)delegates {
    if (_delegates == nil) {
        self.delegates = [NSHashTable weakObjectsHashTable];
    }
    return _delegates;
}

- (void)addDelegate:(id<DoraemonMultiNetworkInterceptorDelegate>) delegate {
    if (![self.delegates containsObject:delegate]) {
        [self.delegates addObject:delegate];
        [self updateURLProtocolInterceptStatus];
    }

}

- (void)removeDelegate:(id<DoraemonMultiNetworkWeakDelegate>)delegate {
    [self.delegates removeObject:delegate];
    [self updateURLProtocolInterceptStatus];
}
    

+ (instancetype)shareInstance {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[DoraemonMultiNetworkInterceptor alloc] init];
    });
    return instance;
}





- (void)updateURLProtocolInterceptStatus {
    if (self.shouldIntercept) {
        [NSURLProtocol registerClass:[DoraemonMultiURLProtocol class]];
    }else{
        [NSURLProtocol unregisterClass:[DoraemonMultiURLProtocol class]];
    }
}

- (void)updateInterceptStatusForSessionConfiguration: (NSURLSessionConfiguration *)sessionConfiguration {
    //BOOL shouldIntercept = [self shouldIntercept];
    if ([sessionConfiguration respondsToSelector:@selector(protocolClasses)]
        && [sessionConfiguration respondsToSelector:@selector(setProtocolClasses:)]) {
        NSMutableArray * urlProtocolClasses = [NSMutableArray arrayWithArray: sessionConfiguration.protocolClasses];
        Class protoCls = DoraemonMultiURLProtocol.class;
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
    dispatch_async(dispatch_get_main_queue(), ^{
        for (id<DoraemonMultiNetworkInterceptorDelegate> delegate in self.delegates) {
            [delegate doraemonNetworkInterceptorDidReceiveData:data response:response request:request error:error startTime:startTime];
        }
    });
}


//重新去请求 dokit
- (void)handleResultWithRequest: (NSURLRequest *)request
                       response: (NSURLResponse *)response
                            sus:(void(^)(id  _Nonnull responseObject))sus
                           fail:(void(^)(NSError *error))fail
                       
{

    for (id<DoraemonMultiNetworkInterceptorDelegate> delegate in self.delegates) {
        [delegate doraemonNetworkResponseModifiyRequest:request response:response sus:^(id  _Nonnull responseObject){
            if(sus){
                sus(responseObject);
            }
        } fail:^(NSError * _Nonnull error){
            if(fail){
                fail(error);
            }
        }];
        
    }
}


@end
