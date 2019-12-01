//
//  DoraemonNetFlowManager.m
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNetFlowManager.h"
#import "DoraemonNSURLProtocol.h"
#import "DoraemonNetFlowDataSource.h"
#import "NSObject+Doraemon.h"
#import "DoraemonNetworkInterceptor.h"
#import "UIViewController+Doraemon.h"

@interface DoraemonNetFlowManager() <DoraemonNetworkInterceptorDelegate>

@end

@implementation DoraemonNetFlowManager

+ (DoraemonNetFlowManager *)shareInstance{
    static dispatch_once_t once;
    static DoraemonNetFlowManager *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetFlowManager alloc] init];
    });
    return instance;
}

- (void)canInterceptNetFlow:(BOOL)enable{
    _canIntercept = enable;
    if (enable) {
        [DoraemonNetworkInterceptor.shareInstance addDelegate:self];
        _startInterceptDate = [NSDate date];
    }else{
        [DoraemonNetworkInterceptor.shareInstance removeDelegate:self];
        _startInterceptDate = nil;
        [[DoraemonNetFlowDataSource shareInstance] clear];
    }
}


#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    DoraemonNetFlowHttpModel *httpModel = [DoraemonNetFlowHttpModel dealWithResponseData:data response:response request:request];
    if (!response) {
        httpModel.statusCode = error.localizedDescription;
    }
    httpModel.startTime = startTime;
    httpModel.endTime = [[NSDate date] timeIntervalSince1970];
    
    httpModel.totalDuration = [NSString stringWithFormat:@"%f",[[NSDate date] timeIntervalSince1970] - startTime];
    httpModel.topVc = NSStringFromClass([[UIViewController topViewControllerForKeyWindow] class]);
    
    [[DoraemonNetFlowDataSource shareInstance] addHttpModel:httpModel];
}

- (BOOL)shouldIntercept {
    return _canIntercept;
}

@end
