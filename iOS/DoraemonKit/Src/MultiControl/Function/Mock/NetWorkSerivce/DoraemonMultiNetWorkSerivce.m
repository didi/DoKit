//
//  DoraemonMultiNetWorkSerivce.m
//  DoraemonKit
//
//  Created by wzp on 2021/9/29.
//

#import "DoraemonMultiNetWorkSerivce.h"
#import <AFNetworking/AFNetworking.h>
#import "DoraemonAppInfoUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonUtil.h"
#import "DoraemonDefine.h"
#import "DoraemonAppInfoUtil.h"
#import "DoraemonManager.h"
#import "DoraemMultiMockManger.h"
@interface MultiNetworking : NSObject

@end

@implementation MultiNetworking

+ (void)requestPostWithApi:(NSString *)api
                    params:(NSDictionary *)params
                       sus:(void(^)(id responseObject))sus
                      fail:(void(^)(NSError *error))fail
{
    AFHTTPSessionManager *manager =  [AFHTTPSessionManager manager];
    manager.requestSerializer = [[AFJSONRequestSerializer alloc]init];
    manager.responseSerializer =  [[AFJSONResponseSerializer alloc]init];
    
    if(![manager.responseSerializer.acceptableContentTypes containsObject:@"application/json"]) {
        NSMutableSet *set = [NSMutableSet setWithSet:manager.responseSerializer.acceptableContentTypes];
        [set addObject:@"application/json"];
        manager.responseSerializer.acceptableContentTypes = [set copy];
    }
    NSString *url = @"http://www.dokit.cn/";
    url = [NSString stringWithFormat:@"%@%@",url, api];
    
    [manager POST:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
        if (sus) {
            sus(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (fail) {
            fail(error);
        }
    }];
    
//    [manager POST:url parameters:params headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
//        if (sus) {
//            sus(responseObject);
//        }
//    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
//        if (fail) {
//            fail(error);
//        }
//    }];
    
}

+ (void)requestGETWithApi:(NSString *)api
                   params:(NSDictionary *)params
                      sus:(void(^)(id responseObject))sus
                     fail:(void(^)(NSError *error))fail
{
    AFHTTPSessionManager *manager =  [AFHTTPSessionManager manager];
    manager.requestSerializer = [[AFJSONRequestSerializer alloc]init];
    manager.responseSerializer =  [[AFJSONResponseSerializer alloc]init];

    if(![manager.responseSerializer.acceptableContentTypes containsObject:@"application/json"]) {
        NSMutableSet *set = [NSMutableSet setWithSet:manager.responseSerializer.acceptableContentTypes];
        [set addObject:@"application/json"];
        manager.responseSerializer.acceptableContentTypes = [set copy];
    }
    NSString *url = @"http://www.dokit.cn/";
    url = [NSString stringWithFormat:@"%@%@",url, api];

    
    [manager GET:url parameters:params success:^(NSURLSessionDataTask * _Nonnull task, id  _Nonnull responseObject) {
        if (sus) {
            sus(responseObject);
        }
    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
        if (fail) {
            fail(error);
        }
    }];
    
    
//    [manager GET:url parameters:params headers:nil progress:nil success:^(NSURLSessionDataTask * _Nonnull task, id  _Nullable responseObject) {
//        if (sus) {
//            sus(responseObject);
//        }
//    } failure:^(NSURLSessionDataTask * _Nullable task, NSError * _Nonnull error) {
//        if (fail) {
//            fail(error);
//        }
//    }];
    
}
 

@end

@implementation DoraemonMultiNetWorkSerivce

/*
 * 获取全局配置
 * /app/multiControl/getConfig
 */
+ (void)multiControlGetConfigWithSus:(void(^)(id responseObject))sus
                                fail:(void(^)(NSError *error))fail {

    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    
    [MultiNetworking requestPostWithApi:@"app/multiControl/getConfig" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
        
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}


/*
 * 开始录制
 * /app/multiControl/startRecord
 */
+ (void)startRecordRequestWithSus:(void(^)(id responseObject))sus
                             fail:(void(^)(NSError *error))fail  {
        
    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    [param setValue:@"iOS" forKey:@"platform"];
    [param setValue:[DoraemonUtil currentTimeInterval] forKey:@"time"];
    [param setValue:[DoraemonAppInfoUtil iphoneType] forKey:@"phoneModel"];
    [param setValue:[DoraemonAppInfoUtil iphoneSystemVersion] forKey:@"systemVersion"];
    [param setValue:[DoraemonAppInfoUtil appName] forKey:@"appName"];
    [param setValue:DoKitVersion forKey:@"dokitVersion"];
    
    
    [MultiNetworking requestPostWithApi:@"app/multiControl/startRecord" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}


/*
 * 接口信息上传
 * /app/multiControl/uploadApiInfo
 */
+ (void)uploadApiInfoWithItem:(DoraemMultiItem *)item
                           sus:(void(^)(id responseObject))sus
                          fail:(void(^)(NSError *error))fail {
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:item.pId forKey:@"pId"];
    [param setValue:item.caseId forKey:@"caseId"];
    [param setValue:item.key forKey:@"key"];
    [param setValue:item.contentType forKey:@"contentType"];
    [param setValue:item.method forKey:@"method"];
    [param setValue:item.path forKey:@"path"];
    [param setValue:item.requestBody forKey:@"requestBody"];
    [param setValue:item.responseBody forKey:@"responseBody"];
    [param setValue:item.query forKey:@"query"];
    [param setValue:item.originKey forKey:@"originKey"];
    [param setValue:item.fragment forKey:@"fragment"];
    
    
    
    [MultiNetworking requestPostWithApi:@"app/multiControl/uploadApiInfo" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}

/*
 * 接口信息上传
 * /app/multiControl/uploadApiInfo
 */
+ (void)uploadApiInfoWithCaseID:(NSString *)caseID
                            key:(NSString *)key
                           path:(NSString *)path
                            sus:(void(^)(id responseObject))sus
                           fail:(void(^)(NSError *error))fail {
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    [param setValue:caseID forKey:@"caseId"];
    [param setValue:key forKey:@"key"];
    [param setValue:path forKey:@"path"];
    
    [MultiNetworking requestPostWithApi:@"app/multiControl/uploadApiInfo" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}

/*
 * 结束录制
 * /app/multiControl/endRecord
 */
+ (void)endRecordWithCaseID:(NSString *)caseID
                 personName:(NSString *)personName
                   caseName:(NSString *)caseName
                        sus:(void(^)(id responseObject))sus
                       fail:(void(^)(NSError *error))fail{
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    [param setValue:caseID forKey:@"caseId"];
    [param setValue:personName forKey:@"personName"];
    [param setValue:caseName forKey:@"caseName"];
    
    [MultiNetworking requestPostWithApi:@"app/multiControl/endRecord" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}


/*
 * 获取测试用例列表
 * /app/multiControl/caseList
 */
+ (void)getCaseListWithSus:(void(^)(id responseObject))sus
                      fail:(void(^)(NSError *error))fail {
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    
    [MultiNetworking requestGETWithApi:@"app/multiControl/caseList" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}


/*
 * 获取用例接口列表
 * /app/multiControl/getCaseApiList
 */
+ (void)getCaseApiListWithCaseId:(NSString *)caseId
                             sus:(void(^)(id responseObject))sus
                            fail:(void(^)(NSError *error))fail {
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
//    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    [param setValue:caseId forKey:@"caseId"];
    
    [MultiNetworking requestGETWithApi:@"multiControl/getCaseApiList" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }
    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
}



/*
 * 获取用例接口列表
 * /app/multiControl/getCaseApiInfo
 */
+ (void)getCaseApiInfoWithCaseID:(NSString *)caseID key:(NSString *)key sus:(void(^)(id responseObject))sus fail:(void(^)(NSError *error))fail{
    
    NSMutableDictionary *param =  [NSMutableDictionary new];
//    [param setValue:[DoraemonManager shareInstance].pId forKey:@"pId"];
    [param setValue:caseID forKey:@"caseID"];
//    [param setValue:key forKey:@"key"];
    
    
    [MultiNetworking requestGETWithApi:@"app/multiControl/getCaseApiInfo" params:param sus:^(id responseObject) {
        NSLog(@"responseObject == %@",responseObject);
        if(sus){
            sus(responseObject);
        }

    } fail:^(NSError *error) {
        NSLog(@"error == %@",error);
        if(fail){
            fail(error);
        }
    }];
    
}



@end
