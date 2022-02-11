//
//  DoraemonMultiURLProtocol.m
//  DoraemonKit
//
//  Created by wzp on 2021/9/18.
//

#import "DoraemonMultiURLProtocol.h"
#import "DoraemonMultiNetworkInterceptor.h"
#import "DoraemonMultiURLSession.h"
#import "DoraemMultiMockManger.h"
//NS_ASSUME_NONNULL_BEGIN

static NSString * const kDoraemonMultiProtocolKey = @"doraemon_multi_protocol_key";

@interface DoraemonMultiURLProtocol()<NSURLSessionDataDelegate>

@property (nonatomic, strong) NSURLSession * urlSession;
@property (nonatomic, assign) NSTimeInterval startTime;
@property (nonatomic, strong) NSURLResponse *response;
@property (nonatomic, strong) NSMutableData *data;
@property (nonatomic, strong) NSMutableData *mockData;
@property (nonatomic, strong) NSError *error;

@property (atomic, strong, readwrite) NSThread *clientThread;
@property (atomic, strong, readwrite) NSArray *modes;
@property (atomic, strong, readwrite) NSURLSessionDataTask *task;

@end

@implementation DoraemonMultiURLProtocol

+ (DoraemonMultiURLSession *)shareSession {
    static dispatch_once_t onceToken;
    static DoraemonMultiURLSession  *session;
    dispatch_once(&onceToken, ^{
        session = [[DoraemonMultiURLSession alloc]initWithConfiguration:[NSURLSessionConfiguration defaultSessionConfiguration]];
    });
    
    return session;
}

#pragma mark - 上报请求 -


#pragma mark - 重写 -

+ (BOOL)canInitWithTask:(NSURLSessionTask *)task {
    NSURLRequest *request = task.currentRequest;
    return request == nil ? NO : [self canInitWithRequest:request];
}
/*
 * 在拦截到网络请求后会调用这一方法，可以再次处理拦截的逻辑，比如设置只针对 http 和 https 的请求进行处理。
 */
+ (BOOL)canInitWithRequest:(NSURLRequest *)request {
    if([NSURLProtocol propertyForKey:kDoraemonMultiProtocolKey inRequest:request]) {
        return NO;
    }
    if (![DoraemonMultiNetworkInterceptor shareInstance].shouldIntercept) {
        return NO;
    }
    
    if([request.URL.host isEqualToString:@"www.dokit.cn"]) {
        return NO;
    }
    
    if (![request.URL.scheme isEqualToString:@"http"] &&
        ![request.URL.scheme isEqualToString:@"https"]) {
        return NO;
    }
    // 文件类型不作处理
    NSString * contentType = [request valueForHTTPHeaderField:@"Content-Type"];
    if (contentType && [contentType containsString:@"multipart/form-data"]) {
        return NO;
    }
    
    return YES;
}

/*
 * 【关键方法】可以在此对 request 进行处理，比如修改地址、提取请求信息、设置请求头等。
 */
+ (NSURLRequest *)canonicalRequestForRequest:(NSURLRequest *)request {
    NSMutableURLRequest *mutableRequest = [request mutableCopy];
    [NSURLProtocol setProperty:@YES forKey:kDoraemonMultiProtocolKey inRequest:mutableRequest];
    return [mutableRequest copy];
}

/*
 * 主要判断两个 request 是否相同，如果相同的话可以使用缓存数据，通常只需要调用父类的实现。
 */
+ (BOOL)requestIsCacheEquivalent:(NSURLRequest *)a toRequest:(NSURLRequest *)b {
    return [super requestIsCacheEquivalent:a toRequest:b];
}

/*
 *  开始请求
 */
- (void)startLoading {
    
    if ([DoraemMultiMockManger sharedInstance].isResponseModifiy) {
        //去dokit 服务端 去请求数据
        dispatch_semaphore_t semaphore = dispatch_semaphore_create(0);

        
        [[DoraemonMultiNetworkInterceptor shareInstance]handleResultWithRequest:[self request]
                                         response: nil sus:^(id  responseObject) {
            
            if ([responseObject isKindOfClass:[NSDictionary class]]) {
                
                NSDictionary *dataDict = [responseObject objectForKey:@"data"];
                if ([dataDict isKindOfClass:[NSDictionary class]]) {
                    NSString * responseBody = [dataDict objectForKey:@"responseBody"];
                    if(responseBody && responseBody.length > 0) {
                        self.mockData = [responseBody dataUsingEncoding:NSUTF8StringEncoding];
                    }else {
                        self.mockData  = nil;
                    }
                }else {
                    self.mockData  = nil;
                }
            }else{
                self.mockData  = nil;
            }
            
            dispatch_semaphore_signal(semaphore);
            
        } fail:^(NSError * _Nonnull error) {
            self.mockData  = nil;
            dispatch_semaphore_signal(semaphore);
        }];
        
        dispatch_semaphore_wait(semaphore, DISPATCH_TIME_FOREVER);
    }else {
        self.mockData =  nil;
    }
    
    NSMutableURLRequest *   mutableRequest;
    NSMutableArray *        calculatedModes;
    NSString *              currentMode;
    
    assert(self.clientThread == nil);
    assert(self.task == nil);
    assert(self.modes == nil);
    
    calculatedModes = [NSMutableArray array];
    [calculatedModes addObject:NSDefaultRunLoopMode];
    currentMode = [[NSRunLoop currentRunLoop]currentMode];
    if (currentMode && ![currentMode isEqual:NSDefaultRunLoopMode]) {
        [calculatedModes addObject:currentMode];
    }
    self.modes = calculatedModes;
    assert(self.modes.count);
    
    mutableRequest  = [[self request] mutableCopy];
    assert(mutableRequest != nil);
    
    self.clientThread = [NSThread currentThread];
    self.data = [NSMutableData data];
    self.startTime = [[NSDate date]timeIntervalSince1970];
    
    self.task = [[[self class] shareSession] dataTaskWithRequest:mutableRequest delegate:self modes:self.modes];
   
    assert(self.task != nil);
    [self.task resume];
    
}


/*
 * 停止请求 处理结束后停止相应请求，清空 connection 或 session
 */
- (void)stopLoading {
    assert(self.clientThread != nil);
    assert([NSThread currentThread] == self.clientThread);
    
    
    //请求返回的数据是self.data
    //response 请求  request返回
    if ([DoraemMultiMockManger sharedInstance].isResponseModifiy) {
        //重新请求
        
        

    }else{
        
        [[DoraemonMultiNetworkInterceptor shareInstance] handleResultWithData:self.data
                                                                     response:self.response
                                                                      request:self.request
                                                                        error:self.error
                                                                    startTime:self.startTime];
        
        
        
    }
    
    if (self.task != nil) {
        [self.task cancel];
        self.task = nil;
    }
}


- (BOOL)needLoading {
    BOOL result = YES;
    
    return result;
}

#pragma mark - NSURLSessionDelegate

/*
 * 通知>>服务器返回响应头
 * 询问>>下一步操作
 * 服务器返回响应头、询问下一步操作(取消操作、普通传输、下载、数据流传输)
 */
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition))completionHandler {
    
    assert(self.clientThread == [NSThread currentThread]);
    self.response = response;
    if ([self needLoading]) {
        [self.client URLProtocol:self didReceiveResponse:response cacheStoragePolicy:NSURLCacheStorageNotAllowed];
        completionHandler(NSURLSessionResponseAllow);
    }
}



/*
 * 通知>>服务器成功返回数据
 *
 */
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data {
    assert([NSThread currentThread] ==  self.clientThread);
    //弱网环境
    if ([DoraemonMultiNetworkInterceptor shareInstance].weakDelegate) {
        if(DoraemonMultiWeakNetwork_WeakSpeed == [[DoraemonMultiNetworkInterceptor shareInstance].weakDelegate weakNetSelecte]){
            [[DoraemonMultiNetworkInterceptor shareInstance].weakDelegate handleWeak:data isDown:YES];
        }
    }

    
    if ([DoraemMultiMockManger sharedInstance].isResponseModifiy) {
        if(self.mockData && self.mockData.length > 5){
            [self.data appendData:self.mockData];
            [self.client URLProtocol:self didLoadData:self.mockData];
        }else{
            [self.data appendData:data];
            [self.client URLProtocol:self didLoadData:data];
        }
        
    }else{
        [self.data appendData:data];
        [self.client URLProtocol:self didLoadData:data];
    }

    return;


}



/*
 * 通知>>任务完成
 * 无论成功、失败或者取消
 */
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error {
    assert([NSThread currentThread] ==  self.clientThread);
    if (error) {
        self.error = error;
        [self.client URLProtocol:self didFailWithError:error];
    }else if([self needLoading]) {
        [self.client URLProtocolDidFinishLoading:self];
    }
}

@end

//NS_ASSNUM_NONONULL_END
