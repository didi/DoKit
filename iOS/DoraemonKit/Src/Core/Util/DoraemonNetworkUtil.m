//
//  DoraemonNetworkUtil.m
//  AFNetworking
//
//  Created by yixiang on 2019/2/28.
//

#import "DoraemonNetworkUtil.h"

#define Doraemon_Server_IP @"https://doraemon.xiaojukeji.com"

@interface DoraemonNetworkUtil()<NSURLSessionDelegate>

@end

@implementation DoraemonNetworkUtil

+ (nonnull DoraemonNetworkUtil *)shareInstance{
    static dispatch_once_t once;
    static DoraemonNetworkUtil *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetworkUtil alloc] init];
    });
    return instance;
}

+ (void)requestURL:(NSString *)url param:(NSDictionary *)param success:(DoraemonNetworkSucceedCallback)successAction
             error:(DoraemonNetworkFailureCallback)errorAction{
    NSError *error;
    if (!param) {
        param = @{};
    }
    NSData *postData = [NSJSONSerialization dataWithJSONObject:param options:0 error:&error];
    if (!error) {
        NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:[NSURL URLWithString:url]];
        request.HTTPMethod = @"POST";
        [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
        [request setHTTPBody:postData];
        
        NSURLSession *session = [NSURLSession sharedSession];
        NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
            dispatch_async(dispatch_get_main_queue(), ^{
                if (error) {
                    errorAction(error);
                }else{
                    NSError *error2;
                    id result = [NSJSONSerialization JSONObjectWithData:data options:0 error:&error2];
                    if (!error2) {
                        successAction(result);
                    }
                }
            });
        }];
        [task resume];
    }
}

+ (void)requestPath:(NSString *)path param:(NSDictionary *)param success:(DoraemonNetworkSucceedCallback)successAction
              error:(DoraemonNetworkFailureCallback)errorAction{
    NSMutableString *str = [NSMutableString stringWithString:Doraemon_Server_IP];
    [str appendString:path];
    [DoraemonNetworkUtil requestURL:str param:param success:successAction error:errorAction];
}

// get 请求
+ (void)getWithUrlString:(NSString *)url params:(NSDictionary *)params success:(DoraemonNetworkSucceedCallback)successAction
                   error:(DoraemonNetworkFailureCallback)errorAction{
    NSMutableString *mutableUrl = [[NSMutableString alloc] initWithString:url];
    if ([params allKeys]) {
        [mutableUrl appendString:@"?"];
        for (id key in params) {
            NSString *value = [[params objectForKey:key] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
            [mutableUrl appendString:[NSString stringWithFormat:@"%@=%@&", key, value]];
        }
    }
    NSString *urlEnCode = [[mutableUrl substringToIndex:mutableUrl.length - 1] stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
    NSURLRequest *request = [NSURLRequest requestWithURL:[NSURL URLWithString:urlEnCode]];
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config delegate:[self shareInstance] delegateQueue:queue];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            errorAction(error);
        } else {
            NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            successAction(dic);
        }
    }];
    [dataTask resume];
}

// post 请求
+ (void)postWithUrlString:(NSString *)url params:(NSDictionary *)params success:(DoraemonNetworkSucceedCallback)successAction
                    error:(DoraemonNetworkFailureCallback)errorAction{
    NSURL *nsurl = [NSURL URLWithString:url];
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:nsurl];
    //设置请求方式
    request.HTTPMethod = @"POST";
    NSString *postStr = [self parseParams:params];
    //设置请求体
    request.HTTPBody = [postStr dataUsingEncoding:NSUTF8StringEncoding];
    NSOperationQueue *queue = [[NSOperationQueue alloc] init];
    NSURLSessionConfiguration *config = [NSURLSessionConfiguration defaultSessionConfiguration];
    NSURLSession *session = [NSURLSession sessionWithConfiguration:config delegate:[self shareInstance] delegateQueue:queue];
    NSURLSessionDataTask *dataTask = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
        if (error) {
            errorAction(error);
        } else {
            NSDictionary *dic = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
            successAction(dic);
        }
    }];
    [dataTask resume];
}

//把NSDictionary解析成post格式的NSString字符串
+ (NSString *)parseParams:(NSDictionary *)params{
    NSString *keyValueFormat;
    NSMutableString *result = [NSMutableString new];
    NSMutableArray *array = [NSMutableArray new];
    //实例化一个key枚举器用来存放dictionary的key
    NSEnumerator *keyEnum = [params keyEnumerator];
    id key;
    while (key = [keyEnum nextObject]) {
        keyValueFormat = [NSString stringWithFormat:@"%@=%@&", key, [params valueForKey:key]];
        [result appendString:keyValueFormat];
        [array addObject:keyValueFormat];
    }
    return result;
}

#pragma mark - NSURLSessionDelegate 代理方法

//主要就是处理HTTPS请求的
- (void)URLSession:(NSURLSession *)session didReceiveChallenge:(NSURLAuthenticationChallenge *)challenge completionHandler:(void (^)(NSURLSessionAuthChallengeDisposition, NSURLCredential *))completionHandler
{
    NSURLProtectionSpace *protectionSpace = challenge.protectionSpace;
    if ([protectionSpace.authenticationMethod isEqualToString:NSURLAuthenticationMethodServerTrust]) {
        SecTrustRef serverTrust = protectionSpace.serverTrust;
        completionHandler(NSURLSessionAuthChallengeUseCredential, [NSURLCredential credentialForTrust:serverTrust]);
    } else {
        completionHandler(NSURLSessionAuthChallengePerformDefaultHandling, nil);
    }
}

@end
