//
//  DoraemonMultiURLSession.m
//  DoraemonKit
//
//  Created by wzp on 2021/9/23.
//

#import "DoraemonMultiURLSession.h"




@interface DoraemonMultiURLSessionTaskInfo()

@property (atomic, strong, readonly) NSURLSessionDataTask  *task;
@property (atomic, strong, readonly) id<NSURLSessionDataDelegate> delegate;
@property (atomic, strong, readonly) NSThread *     thread;
@property (atomic, strong, readonly) NSArray *      modes;

@property (atomic, strong) NSURLRequest  *request;

@end

//@interface DoraemonMultiURLSessionTaskInfo : <#superclass#>
//
//@end


@implementation DoraemonMultiURLSessionTaskInfo

- (instancetype)initWithTask:(NSURLSessionDataTask *)task delegate:(id <NSURLSessionDataDelegate>)delegate modes:(NSArray *)modes;
{
    NSAssert(task, @"task will  nil");
    NSAssert(delegate, @"delegate will nil");
    NSAssert(modes, @" modes will nil");
    
    self = [super init];
    if (self != nil) {
        self->_task = task;
        self->_delegate = delegate;
        self->_thread = [NSThread currentThread];
        self->_modes = [modes copy];
    }
    
    return self;
}

- (void)performBlock:(dispatch_block_t)block {
    
    NSAssert(self.delegate, @"delegate will nil");
    NSAssert(self.thread, @"thread will nil");
    
    [self performSelector:@selector(performBlockOnClientThread:) onThread:self.thread withObject:[block copy] waitUntilDone:NO modes:self.modes];
}

- (void)performBlockOnClientThread:(dispatch_block_t)block {
    NSAssert([NSThread currentThread] == self.thread, @"self.thread  not [NSThread currentThread]");
    block();
    
}

- (void)invalidate {
    self->_delegate = nil;
    self->_thread = nil;
}

@end


@interface DoraemonMultiURLSession () <NSURLSessionDataDelegate>

@property (atomic, strong, readonly) NSMutableDictionary *taskInfoByTaskID;
@property (atomic, strong, readonly) NSOperationQueue *seesionDelegateQueue;


@end

@implementation DoraemonMultiURLSession

- (instancetype)init {
   return [self initWithConfiguration:nil];
}

- (instancetype)initWithConfiguration:(nullable NSURLSessionConfiguration *)configuration {
    self = [super init];
    if(self != nil) {
        if(!configuration) {
            configuration =  [NSURLSessionConfiguration defaultSessionConfiguration];
        }
        self->_configuration = [configuration copy];
        self->_taskInfoByTaskID =  [[NSMutableDictionary alloc]init];
        self->_seesionDelegateQueue = [[NSOperationQueue alloc]init];
        [self.seesionDelegateQueue setName:NSStringFromClass([DoraemonMultiURLSession class])];
        [self.seesionDelegateQueue setMaxConcurrentOperationCount:1];
        self.seesion.sessionDescription = NSStringFromClass([DoraemonMultiURLSession class]);
        self->_seesion = [NSURLSession sessionWithConfiguration:self->_configuration delegate:self delegateQueue:self->_seesionDelegateQueue];
        
    }
    
    return self;
}

-(NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request delegate:(id <NSURLSessionDataDelegate>)delegate modes:(NSArray *)modes {
    
    NSAssert(request, @"request will nil");
    NSAssert(delegate, @"delegate will nil");
    
    if (modes.count == 0) {
        modes = @[NSDefaultRunLoopMode];
    }
    NSURLSessionDataTask * task = [self.seesion dataTaskWithRequest:request];
    NSAssert(task, @"task create will nil");
    
    DoraemonMultiURLSessionTaskInfo * taskInfo = [[DoraemonMultiURLSessionTaskInfo alloc]initWithTask:task delegate:delegate modes:modes];
    taskInfo.request = request;
    
    @synchronized (self) {
        self.taskInfoByTaskID[@(task.taskIdentifier)] = taskInfo;
    }

    return task;
    
}

- (DoraemonMultiURLSessionTaskInfo *)taskInfoForTask:(NSURLSessionTask *)task {
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    NSAssert(task, @"taskInfoForTask task will nil");
    
    @synchronized (self) {
        taskInfo = self.taskInfoByTaskID[@(task.taskIdentifier)];
        NSAssert(taskInfo, @"taskInfoForTask taskInfo will nil");
    }
    return taskInfo;
}

#pragma mark -- NSURLSessionDataDelegate

/*
 *  准备开始请求，询问是否重定向
 */
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task willPerformHTTPRedirection:(NSHTTPURLResponse *)response newRequest:(NSURLRequest *)newRequest completionHandler:(void (^)(NSURLRequest *))completionHandler {
    
    DoraemonMultiURLSessionTaskInfo *taskInfo = [self taskInfoForTask:task];
    
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:task:willPerformHTTPRedirection:newRequest:completionHandler:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session task:task willPerformHTTPRedirection:response newRequest:newRequest completionHandler:completionHandler];
        }];
    }else {
        completionHandler(newRequest);
    }
}

/*
 *  询问 服务端客户端匹配验证 -- 会话级别的
 */
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition disposition, NSURLCredential *credential))completionHandler {
    
    DoraemonMultiURLSessionTaskInfo *taskInfo = [self taskInfoForTask:task];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:task:didReceiveChallenge:completionHandler:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session task:task didReceiveChallenge:challenge completionHandler:completionHandler];
        }];
    }else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, nil);
    }
    
    
}

/* 询问>>流任务的方式上传--需要客户端提供数据源
 当任务需要新的请求主体流发送到远程服务器时，告诉委托。
 这种委托方法在两种情况下被调用：
 1、如果使用uploadTaskWithStreamedRequest创建任务，则提供初始请求正文流：
 2、如果任务因身份验证质询或其他可恢复的服务器错误需要重新发送包含正文流的请求，则提供替换请求正文流。
 注：如果代码使用文件URL或NSData对象提供请求主体，则不需要实现此功能。
 */
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task needNewBodyStream:(void (^)(NSInputStream *bodyStream))completionHandler{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:task];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:task:needNewBodyStream:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session task:task needNewBodyStream:completionHandler];
        }];
    } else {
        completionHandler(nil);
    }
}


/*
 *  通知>>上传进度
 *  定期通知代理向服务器发送主体内容的进度
 */

- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didSendBodyData:(int64_t)bytesSent totalBytesSent:(int64_t)totalBytesSent totalBytesExpectedToSend:(int64_t)totalBytesExpectedToSend{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:task];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:task:didSendBodyData:totalBytesSent:totalBytesExpectedToSend:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session task:task didSendBodyData:bytesSent totalBytesSent:totalBytesSent totalBytesExpectedToSend:totalBytesExpectedToSend];
        }];
    }
}

/*
 *  通知>>任务完成
 *  无论成功、失败或者取消
 */
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:task];
    @synchronized (self) {
        [self.taskInfoByTaskID removeObjectForKey:@(taskInfo.task.taskIdentifier)];
    }
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:task:didCompleteWithError:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session task:task didCompleteWithError:error];
            [taskInfo invalidate];
        }];
    } else {
        [taskInfo invalidate];
    }
}

/*
 * 通知>>服务器返回响应头
 * 询问>>下一步操作
 * 服务器返回响应头、询问下一步操作(取消操作、普通传输、下载、数据流传输)
 */

- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveResponse:(NSURLResponse *)response completionHandler:(void (^)(NSURLSessionResponseDisposition disposition))completionHandler{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:dataTask];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:dataTask:didReceiveResponse:completionHandler:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session dataTask:dataTask didReceiveResponse:response completionHandler:completionHandler];
        }];
    } else {
        completionHandler(NSURLSessionResponseAllow);
    }
}

/*
 * 通知>>数据任务已更改为下载任务
 */
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didBecomeDownloadTask:(NSURLSessionDownloadTask *)downloadTask{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:dataTask];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:dataTask:didBecomeDownloadTask:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session dataTask:dataTask didBecomeDownloadTask:downloadTask];
        }];
    }
}

/*
 * 通知>>服务器成功返回数据
 */
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask didReceiveData:(NSData *)data{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:dataTask];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:dataTask:didReceiveData:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session dataTask:dataTask didReceiveData:data];
        }];
    }
}

/*
 * 询问>>是否把Response存储到Cache中
 * 任务是否应将响应存储在缓存中
 */
- (void)URLSession:(NSURLSession *)session dataTask:(NSURLSessionDataTask *)dataTask willCacheResponse:(NSCachedURLResponse *)proposedResponse completionHandler:(void (^)(NSCachedURLResponse *cachedResponse))completionHandler{
    DoraemonMultiURLSessionTaskInfo *taskInfo;
    
    taskInfo = [self taskInfoForTask:dataTask];
    if ([taskInfo.delegate respondsToSelector:@selector(URLSession:dataTask:willCacheResponse:completionHandler:)]) {
        [taskInfo performBlock:^{
            [taskInfo.delegate URLSession:session dataTask:dataTask willCacheResponse:proposedResponse completionHandler:completionHandler];
        }];
    } else {
        completionHandler(proposedResponse);
    }
}





@end
