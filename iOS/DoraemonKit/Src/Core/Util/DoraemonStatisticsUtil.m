//
//  DoraemonStatisticsUtil.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/10.
//

#import "DoraemonStatisticsUtil.h"
#import "DoraemonDefine.h"

@implementation DoraemonStatisticsUtil

+ (nonnull DoraemonStatisticsUtil *)shareInstance{
    static dispatch_once_t once;
    static DoraemonStatisticsUtil *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemonStatisticsUtil alloc] init];
    });
    return instance;
}

- (void)upLoadUserInfo{
    if (_noUpLoad) {
        return;
    }
    NSURL *url = [NSURL URLWithString:@"https://doraemon.xiaojukeji.com/uploadAppData"];
    
    NSString *appId = [DoraemonAppInfoUtil bundleIdentifier];
    NSString *appName = [DoraemonAppInfoUtil appName];
    NSString *doKitVersion = DoKitVersion;
    NSString *type = @"iOS";
    NSString *from = @"1";
    
    NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
    [param setValue:appId forKey:@"appId"];
    [param setValue:appName forKey:@"appName"];
    [param setValue:doKitVersion forKey:@"version"];
    [param setValue:type forKey:@"type"];
    [param setValue:from forKey:@"from"];
    NSError *error;
    NSData *postData = [NSJSONSerialization dataWithJSONObject:param options:0 error:&error];
    
    NSMutableURLRequest *request = [NSMutableURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setHTTPBody:postData];
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:^(NSData * _Nullable data, NSURLResponse * _Nullable response, NSError * _Nullable error) {
//        if (error) {
//            NSLog(@"%@",error);
//        }else{
//            NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
//            NSLog(@"%@",str);
//        }
    }];
    [task resume];
}

@end
