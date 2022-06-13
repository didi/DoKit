/**
 * Copyright 2017 Beijing DiDi Infinity Technology and Development Co., Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

#import "DKMultiControlProtocol.h"
#import <DoraemonKit/DKMultiControlStreamManager.h>

NS_ASSUME_NONNULL_BEGIN

static NSString *const ERROR_DOMAIN = @"com.didi.dokit";

static NSString *const MULTI_CONTROL_PROTOCOL_KEY = @"MULTI_CONTROL_PROTOCOL_KEY";

@interface DKMultiControlProtocol () <NSURLSessionDataDelegate>

@property(nonatomic, nullable, weak) NSURLSession *urlSession;

@property(nonatomic, nullable, copy) NSString *dataId;

@property(nonatomic, nullable, copy) NSHTTPURLResponse *httpUrlResponse;

@property(nonatomic, nullable, copy) NSString *responseBody;

@end

NS_ASSUME_NONNULL_END

@implementation DKMultiControlProtocol

+ (BOOL)canInitWithRequest:(NSURLRequest *)request {
    // +[NSURLProtocol canInitWithRequest:] may be called from any thread.
    BOOL returnValue = NO;
    switch (DKMultiControlStreamManager.sharedInstance.state) {
        case DKMultiControlStreamManagerStateMaster:
            if (![NSURLProtocol propertyForKey:MULTI_CONTROL_PROTOCOL_KEY inRequest:request]) {
                NSString *contentType = [request valueForHTTPHeaderField:@"Content-Type"];
                NSString *accept = [request valueForHTTPHeaderField:@"Accept"];
                if (![contentType hasPrefix:@"multipart/form-data"] && ![accept hasPrefix:@"image/"]) {
                    returnValue = YES;
                }
            }
            break;
        case DKMultiControlStreamManagerStateSlave:
            returnValue = YES;
            break;

        default:
            break;
    }

    return returnValue;
}

+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request {
    // +[NSURLProtocol canonicalRequestForRequest:] may be called from any thread.
    if (DKMultiControlStreamManager.sharedInstance.state != DKMultiControlStreamManagerStateMaster) {
        return request;
    }
    NSMutableURLRequest *mutableUrlRequest = request.mutableCopy;
    [NSURLProtocol setProperty:@(YES) forKey:MULTI_CONTROL_PROTOCOL_KEY inRequest:mutableUrlRequest];
    
    return mutableUrlRequest.copy;
}

- (void)startLoading {
    NSOperationQueue *clientOperationQueue = [[NSOperationQueue alloc] init];
    clientOperationQueue.maxConcurrentOperationCount = 1;
    if ([NSURLProtocol propertyForKey:MULTI_CONTROL_PROTOCOL_KEY inRequest:self.request]) {
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);
        dispatch_async(dispatch_get_main_queue(), ^{
            self.dataId = [DKMultiControlStreamManager.sharedInstance recordWithUrlRequest:self.request];
            dispatch_semaphore_signal(semaphore);
        });
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
        self.urlSession = [NSURLSession sessionWithConfiguration:NSURLSessionConfiguration.defaultSessionConfiguration delegate:self delegateQueue:clientOperationQueue];
        [[self.urlSession dataTaskWithRequest:self.request] resume];
    } else {
        // Slave device send request through websocket.
        NSURLRequest *urlRequest = self.request.copy;
        __weak typeof(self) weakSelf = self;
        dispatch_async(dispatch_get_main_queue(), ^{
            [DKMultiControlStreamManager.sharedInstance queryWithUrlRequest:urlRequest completionBlock:^(NSError *error, NSHTTPURLResponse *response, NSData *data) {
                // Main thread.
                [clientOperationQueue addOperationWithBlock:^{
                    typeof(weakSelf) self = weakSelf;
                    if (error || !response) {
                        [self.client URLProtocol:self didFailWithError:error ?: [NSError errorWithDomain:ERROR_DOMAIN code:0 userInfo:nil]];

                        return;
                    }
                    [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
                    if (data) {
                        [self.client URLProtocol:self didLoadData:data];
                    }
                    [self.client URLProtocolDidFinishLoading:self];
                }];
            }];
        });
    }
}

- (void)stopLoading {
    [self.urlSession invalidateAndCancel];
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageAllowed];
    completionHandler(NSURLSessionResponseAllow);
    if ([response isKindOfClass:NSHTTPURLResponse.class]) {
        self.httpUrlResponse = (NSHTTPURLResponse *) response;
    }
}

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    [self.client URLProtocol:self didLoadData:data];
    // TODO(ChasonTang): Append data to `self.responseData`.
    self.responseBody = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition, NSURLCredential *))completionHandler {
    if (challenge.protectionSpace.authenticationMethod == NSURLAuthenticationMethodServerTrust) {
        NSURLCredential *urlCredential = challenge.protectionSpace.serverTrust ? [NSURLCredential credentialForTrust:challenge.protectionSpace.serverTrust] : nil;
        completionHandler(NSURLSessionAuthChallengeUseCredential, urlCredential);
    } else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, nil);
    }
}

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    if (!error) {
        [self.client URLProtocolDidFinishLoading:self];
        if (self.httpUrlResponse && self.dataId) {
            NSHTTPURLResponse *httpUrlResponse = self.httpUrlResponse.copy;
            NSString *dataId = self.dataId.copy;
            NSString *responseBody = self.responseBody.copy;
            dispatch_async(dispatch_get_main_queue(), ^{
                [DKMultiControlStreamManager.sharedInstance recordWithHTTPUrlResponse:httpUrlResponse dataId:dataId responseBody:responseBody];
            });
        }
    } else {
        [self.client URLProtocol:self didFailWithError:error];
    }
}

@end
