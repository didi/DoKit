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
    NSString *url = @"http://doraemon.xiaojukeji.com/uploadAppData";
    
    NSString *appId = [[NSBundle mainBundle] bundleIdentifier];;
    NSString *appName = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleDisplayName"];
    NSString *appVersion = [[NSBundle mainBundle] objectForInfoDictionaryKey:@"CFBundleShortVersionString"];
    NSString *type = @"iOS";
    NSString *from = @"0";
    
    NSMutableDictionary *param = [[NSMutableDictionary alloc] init];
    [param setValue:appId forKey:@"appId"];
    [param setValue:appName forKey:@"appName"];
    [param setValue:appVersion forKey:@"appVersion"];
    [param setValue:type forKey:@"type"];
    [param setValue:from forKey:@"from"];
    
    AFHTTPRequestOperationManager *manager = [AFHTTPRequestOperationManager manager];
    [manager POST:url parameters:param success:^(AFHTTPRequestOperation *operation, id responseObject) {
    } failure:^(AFHTTPRequestOperation *operation, NSError *error) {
    }];
    
}

@end
