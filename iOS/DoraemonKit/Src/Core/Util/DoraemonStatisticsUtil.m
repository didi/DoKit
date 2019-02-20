//
//  DoraemonStatisticsUtil.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/10.
//

#import "DoraemonStatisticsUtil.h"

@implementation DoraemonStatisticsUtil

+ (void)upLoadUserInfo{
    NSString *url = @"https://doraemon.xiaojukeji.com/uploadAppData";
    
    NSString *appId = [[NSBundle mainBundle] bundleIdentifier];;
    NSString *appName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"];
    NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
    NSString *type = @"iOS";
    NSString *from = @"1";
    
    NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
    [param setValue:appId forKey:@"appId"];
    [param setValue:appName forKey:@"appName"];
    [param setValue:appVersion forKey:@"appVersion"];
    [param setValue:type forKey:@"type"];
    [param setValue:from forKey:@"from"];
    NSError *error;
    NSData *postData = [NSJSONSerialization dataWithJSONObject:param options:0 error:&error];
    
    NSMutableURLRequest *request = [NSURLRequest requestWithURL:url];
    request.HTTPMethod = @"POST";
    [request addValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request addValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setHTTPBody:postData];
    
    NSURLSession *session = [NSURLSession sharedSession];
    NSURLSessionDataTask *task = [session dataTaskWithRequest:request completionHandler:nil];
    [task resume];
}

@end
