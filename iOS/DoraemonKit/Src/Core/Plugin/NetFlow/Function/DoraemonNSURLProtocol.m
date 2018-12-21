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

@interface DoraemonNSURLProtocol()<NSURLConnectionDelegate,NSURLConnectionDataDelegate>

@property (nonatomic, strong) NSURLConnection *connection;
@property (nonatomic, assign) NSTimeInterval startTime;
@property (nonatomic, strong) NSURLResponse *response;
@property (nonatomic, strong) NSMutableData *data;
@property (nonatomic, strong) NSError *error;

@end

@implementation DoraemonNSURLProtocol

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
    //NSLog(@"startLoading");
    self.connection = [[NSURLConnection alloc] initWithRequest:[[self class] canonicalRequestForRequest:self.request] delegate:self];
    [self.connection start];
    self.data = [NSMutableData data];
    self.startTime = [[NSDate date] timeIntervalSince1970];
}

- (void)stopLoading{
    //NSLog(@"stopLoading");
    [self.connection cancel];
    DoraemonNetFlowHttpModel *httpModel = [DoraemonNetFlowHttpModel dealWithResponseData:self.data response:self.response request:self.request];
    if (!self.response) {
        httpModel.statusCode = self.error.localizedDescription;
    }
    httpModel.startTime = self.startTime;
    httpModel.endTime = [[NSDate date] timeIntervalSince1970];
    
    httpModel.totalDuration = [NSString stringWithFormat:@"%f",[[NSDate date] timeIntervalSince1970] - self.startTime];
    [[DoraemonNetFlowDataSource shareInstance] addHttpModel:httpModel];
}


#pragma mark - NSURLConnectionDelegate
- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error{
    [[self client] URLProtocol:self didFailWithError:error];
    self.error = error;
}

- (BOOL)connectionShouldUseCredentialStorage:(NSURLConnection *)connection {
    return YES;
}

- (void)connection:(NSURLConnection *)connection didReceiveAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
    [[self client] URLProtocol:self didReceiveAuthenticationChallenge:challenge];
}

- (void)connection:(NSURLConnection *)connection didCancelAuthenticationChallenge:(NSURLAuthenticationChallenge *)challenge {
    [[self client] URLProtocol:self didCancelAuthenticationChallenge:challenge];
}

#pragma mark - NSURLConnectionDataDelegate
- (void)connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response{
    [[self client] URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
    self.response = response;
}

- (void)connection:(NSURLConnection *)connection didReceiveData:(NSData *)data{
    [[self client] URLProtocol:self didLoadData:data];
    [self.data appendData:data];
}

- (NSCachedURLResponse *)connection:(NSURLConnection *)connection willCacheResponse:(NSCachedURLResponse *)cachedResponse{
    return cachedResponse;
}

- (void)connectionDidFinishLoading:(NSURLConnection *)connection {
    [[self client] URLProtocolDidFinishLoading:self];
}

@end
