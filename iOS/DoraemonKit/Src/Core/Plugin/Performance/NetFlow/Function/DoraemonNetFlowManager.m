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
#import "DoraemonHealthManager.h"

@interface DoraemonNetFlowManager() <DoraemonNetworkInterceptorDelegate, NSStreamDelegate>

@property (nonatomic, copy) HttpBodyCallBack bodyCallBack;
@property (nonatomic, strong) NSMutableData *bodyData;

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
        [[DoraemonNetworkInterceptor shareInstance] addDelegate:self];
        _startInterceptDate = [NSDate date];
    }else{
        [DoraemonNetworkInterceptor.shareInstance removeDelegate:self];
        _startInterceptDate = nil;
        [[DoraemonNetFlowDataSource shareInstance] clear];
    }
}

- (void)httpBodyFromRequest:(NSURLRequest *)request bodyCallBack:(HttpBodyCallBack)complete {
    NSData *httpBody = nil;
    if (request.HTTPBody) {
        httpBody = request.HTTPBody;
        complete(httpBody);
        return;
    }
    if ([request.HTTPMethod isEqualToString:@"POST"]) {
        NSInputStream *stream = request.HTTPBodyStream;
        [stream setDelegate:self];
        self.bodyCallBack = complete;
        [stream scheduleInRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
        [stream open];
    } else {
        complete(httpBody);
    }
}

#pragma mark -- NSStreamDelegate

- (void)stream:(NSStream *)aStream handleEvent:(NSStreamEvent)eventCode {
    switch (eventCode) {
        case NSStreamEventHasBytesAvailable:
        {
            if (!self.bodyData) {
                self.bodyData = [NSMutableData data];
            }
            uint8_t buf[1024];
            NSInteger len = 0;
            len = [(NSInputStream *)aStream read:buf maxLength:1024];
            if (len) {
                [self.bodyData appendBytes:(const void *)buf length:len];
            }
        }
            break;
        case NSStreamEventErrorOccurred:
        {
            NSError * error = [aStream streamError];
            NSString * errorInfo = [NSString stringWithFormat:@"Failed while reading stream; error '%@' (code %ld)", error.localizedDescription, error.code];
            NSLog(@"%@",errorInfo);
        }
            break;
        case NSStreamEventEndEncountered:
        {
            [aStream close];
            [aStream removeFromRunLoop:[NSRunLoop currentRunLoop] forMode:NSDefaultRunLoopMode];
            aStream = nil;
            if (self.bodyCallBack) {
                self.bodyCallBack([self.bodyData copy]);
            }
            self.bodyData = nil;
        }
            break;
        default:
            break;
    }
}

#pragma mark -- DoraemonNetworkInterceptorDelegate
- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data response:(NSURLResponse *)response request:(NSURLRequest *)request error:(NSError *)error startTime:(NSTimeInterval)startTime {
    [DoraemonNetFlowHttpModel dealWithResponseData:data response:response request:request complete:^(DoraemonNetFlowHttpModel *httpModel) {
        if (!response) {
            httpModel.statusCode = error.localizedDescription;
        }
        httpModel.startTime = startTime;
        httpModel.endTime = [[NSDate date] timeIntervalSince1970];
        
        httpModel.totalDuration = [NSString stringWithFormat:@"%f",[[NSDate date] timeIntervalSince1970] - startTime];
        httpModel.topVc = NSStringFromClass([[UIViewController topViewControllerForKeyWindow] class]);
        
        [[DoraemonNetFlowDataSource shareInstance] addHttpModel:httpModel];
        [[DoraemonHealthManager sharedInstance] addHttpModel:httpModel];
    }];
}

- (BOOL)shouldIntercept {
    return _canIntercept;
}

@end
