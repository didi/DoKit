//
//  DoraemonDemoURLProtocol2.m
//  DoraemonKitDemo
//
//  Created by didi on 2020/3/19.
//  Copyright © 2020 yixiang. All rights reserved.
//

#import "DoraemonDemoURLProtocol2.h"
#import "DoraemonUrlUtil.h"
#import "DoraemonNetFlowManager.h"

static NSString * const kDoraemonDemoUrlProtocol2Key = @"doraemon_demo_url_protocol_2_key";

@interface DoraemonDemoURLProtocol2()

@property (nonatomic, strong) NSURLConnection *connection;

@end

@implementation DoraemonDemoURLProtocol2

+ (BOOL)canInitWithTask:(NSURLSessionTask *)task {
    NSURLRequest *request = task.currentRequest;
    NSLog(@"22222 == canInitWithTask");
    return request == nil ? NO : [self canInitWithRequest:request];
}

+ (BOOL)canInitWithRequest:(NSURLRequest *)request{
    if ([NSURLProtocol propertyForKey:kDoraemonDemoUrlProtocol2Key inRequest:request]) {
        return NO;
    }
    NSLog(@"22222 == canInitWithRequest");
    return YES;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request{
    NSMutableURLRequest *mutableReqeust = [request mutableCopy];
    [NSURLProtocol setProperty:@YES forKey:kDoraemonDemoUrlProtocol2Key inRequest:mutableReqeust];
    NSLog(@"22222 == canonicalRequestForRequest");
    return [mutableReqeust copy];
}

- (void)startLoading{
    NSMutableURLRequest *mutableReqeust = [[self request] mutableCopy];
    NSLog(@"22222 == startLoading");
    self.connection = [NSURLConnection connectionWithRequest:mutableReqeust delegate:self];
}

- (void)stopLoading{
    NSLog(@"22222 == stopLoading");
    [[DoraemonNetFlowManager shareInstance] httpBodyFromRequest:self.request bodyCallBack:^(NSData *httpBody) {
        NSString* requestBody = [DoraemonUrlUtil convertJsonFromData:httpBody];
        NSLog(@"22222 == requestBody = %@",requestBody);
        [self.connection cancel];
    }];
}



#pragma mark - NSURLSessionDelegate
- (void) connection:(NSURLConnection *)connection didReceiveResponse:(NSURLResponse *)response {
    NSLog(@"22222 == didReceiveResponse");
    [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
}

- (void) connection:(NSURLConnection *)connection didReceiveData:(NSData *)data {
    NSLog(@"22222 == didReceiveData");
    [self.client URLProtocol:self didLoadData:data];
}

- (void) connectionDidFinishLoading:(NSURLConnection *)connection {
    NSLog(@"22222 == connectionDidFinishLoading");
    [self.client URLProtocolDidFinishLoading:self];
}

- (void)connection:(NSURLConnection *)connection didFailWithError:(NSError *)error {
    NSLog(@"22222 == didFailWithError");
    [self.client URLProtocol:self didFailWithError:error];
}

@end
