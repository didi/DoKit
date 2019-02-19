//
//  DoraemonStatisticsUtil.m
//  AFNetworking
//
//  Created by yixiang on 2018/12/10.
//

#import "DoraemonStatisticsUtil.h"
#import <AFNetworking/AFNetworking.h>

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
    
    AFHTTPSessionManager *session = [AFHTTPSessionManager manager];
    session.responseSerializer = [AFJSONResponseSerializer serializer];
    session.responseSerializer.acceptableContentTypes = [NSSet setWithObjects:@"application/json", @"text/json", @"text/javascript",@"text/html",@"text/plain", nil];
    session.requestSerializer=[AFJSONRequestSerializer serializer];
    
    [session POST:url parameters:param success:^(NSURLSessionDataTask *task, id responseObject) {
        
        NSLog(@"responseObject == %@",responseObject);
    } failure:^(NSURLSessionDataTask *task, NSError *error) {
        
        NSLog(@"error == %@",error);
    }];

    
}

@end
