//
//  DoraemonNSURLProtocol.m
//  Aspects
//
//  Created by yixiang on 2018/4/11.
//

#import "DoraemonNSURLProtocol.h"
#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowManager.h"

static NSString * const kDoraemonProtocolKey = @"doraemon_protocol_key";

@interface DoraemonNSURLProtocol()<NSURLSessionDelegate,NSURLSessionTaskDelegate>

@property (nonatomic, strong) NSURLSession *urlSession;
@property (nonatomic, assign) NSTimeInterval startTime;
@property (nonatomic, strong) NSURLResponse *response;
@property (nonatomic, strong) NSMutableData *data;
@property (nonatomic, strong) NSError *error;

@end

@implementation DoraemonNSURLProtocol

+ (BOOL)canInitWithTask:(NSURLSessionTask *)task {
    NSURLRequest *request = task.currentRequest;
    return request == nil ? NO : [self canInitWithRequest:request];
}

+ (BOOL)canInitWithRequest:(NSURLRequest *)request{
    if ([NSURLProtocol propertyForKey:kDoraemonProtocolKey inRequest:request]) {
        return NO;
    }
    if (![DoraemonNetFlowManager shareInstance].canIntercept) {
        return NO;
    }
    if (![request.URL.scheme isEqualToString:@"http"] &&
        ![request.URL.scheme isEqualToString:@"https"]) {
        return NO;
    }
    //NSLog(@"DoraemonNSURLProtocol == %@",request.URL.absoluteString);
    return YES;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request{
    //NSLog(@"canonicalRequestForRequest");
    NSMutableURLRequest *mutableReqeust = [request mutableCopy];
    [NSURLProtocol setProperty:@YES forKey:kDoraemonProtocolKey inRequest:mutableReqeust];
    return [mutableReqeust copy];
}

- (void)startLoading{
    self.data = [NSMutableData data];
    self.startTime = [[NSDate date] timeIntervalSince1970];
    
    
    //NSLog(@"startLoading");
    if (!self.urlSession) {
        self.urlSession = [NSURLSession sessionWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]
                                                        delegate:self
                                                   delegateQueue:nil];
    }
    
    NSMutableURLRequest *request = [[DoraemonNSURLProtocol canonicalRequestForRequest:self.request] mutableCopy];
    
    NSURLSessionDataTask *task = [self.urlSession dataTaskWithRequest:request];
    [task resume];
}

- (void)stopLoading{
    //NSLog(@"stopLoading");
    DoraemonNetFlowHttpModel *httpModel = [DoraemonNetFlowHttpModel dealWithResponseData:self.data response:self.response request:self.request];
    if (!self.response) {
        httpModel.statusCode = self.error.localizedDescription;
    }
    httpModel.startTime = self.startTime;
    httpModel.endTime = [[NSDate date] timeIntervalSince1970];
    
    httpModel.totalDuration = [NSString stringWithFormat:@"%f",[[NSDate date] timeIntervalSince1970] - self.startTime];
    [[DoraemonNetFlowDataSource shareInstance] addHttpModel:httpModel];
    
    [self.urlSession invalidateAndCancel];
    self.urlSession = nil;
}

#pragma mark - NSURLSessionDelegate
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    self.response = response;
    [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
    completionHandler(NSURLSessionResponseAllow);
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    [self.data appendData:data];
    [self.client URLProtocol:self didLoadData:data];
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    if (error) {
        self.error = error;
        [self.client URLProtocol:self didFailWithError:error];
    }else{
        [self.client URLProtocolDidFinishLoading:self];
    }
}

- (void)URLSession:(NSURLSession *)session didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition, NSURLCredential * _Nullable))completionHandler {
    //判断服务器返回的证书类型, 是否是服务器信任
    if ([challenge.protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]) {
        //强制信任
        NSURLCredential *card = [[NSURLCredential alloc]initWithTrust:challenge.protectionSpace.serverTrust];
        completionHandler(NSURLSessionAuthChallengeUseCredential, card);
    }
}

@end
