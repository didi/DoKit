//
//  DoraemMultiMockManger.m
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import "DoraemMultiMockManger.h"
#import "DoraemonMultiNetworkInterceptor.h"
#import "DoraemonManager.h"
#import<CommonCrypto/CommonDigest.h>
#import "DoraemonMultiNetWorkSerivce.h"
#import "DoraemonUrlUtil.h"
// 单例



@implementation DoraemMultiItem



@end

@interface DoraemMultiMockManger() 

@end


@implementation DoraemMultiMockManger

+ (DoraemMultiMockManger *)sharedInstance {
    static dispatch_once_t once;
    static DoraemMultiMockManger *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemMultiMockManger alloc] init];
    });
    return instance;
}


#pragma  mark -DoraemonMultiNetworkInterceptorDelegate-


- (BOOL)shouldIntercept
{
    return YES;
}

- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data
                                        response:(NSURLResponse *)response
                                         request:(NSURLRequest *)request
                                           error:(NSError *)error
                                       startTime:(NSTimeInterval)startTime {
    
    if (self.isResponseModifiy) {
        
        return;
    }
    
    DoraemMultiItem  *item = [self getDoraemMultiItemWithData:data request:request response:response];
    
    [DoraemonMultiNetWorkSerivce uploadApiInfoWithItem:item sus:^(id  _Nonnull responseObject) {
        
    } fail:^(NSError * _Nonnull error) {
        
    }];

    
}


//正常请求发出去以后 返回了，然后在去请求云服务
- (void)doraemonNetworkResponseModifiyRequest:(NSURLRequest *)request
                                     response:(NSURLResponse *)response
                                          sus:(void(^)(id  _Nonnull responseObject))sus
                                         fail:(void(^)(NSError *error))fail
{
    
    //请求
    DoraemMultiItem *item = [self getDoraemMultiItemWithData:nil request:request response:response];
    NSString *key = [self getKeyWithDoraemMultiItem:item];
    
    [DoraemonMultiNetWorkSerivce getCaseApiInfoWithCaseID:[DoraemMultiMockManger sharedInstance].selectCaseId key: key  sus:^(id  _Nonnull responseObject) {
        
        if (sus) {
            sus(responseObject);

        }
    } fail:^(NSError * _Nonnull error) {
        if (fail) {
            fail(error);
        }
    }];
    
}


- (DoraemMultiItem *)getDoraemMultiItemWithData:(NSData *)data
                                        request:(NSURLRequest *)request
                                       response:(NSURLResponse *)response {
    

    //收集所有的数据
    NSString *responseBody = [[NSString alloc]initWithData:data encoding:NSUTF8StringEncoding];
    
    uint8_t sub[1024] = {0};
    NSInputStream *inputStream = request.HTTPBodyStream;
    NSMutableData *body = [[NSMutableData alloc] init];
    [inputStream open];
    while ([inputStream hasBytesAvailable]) {
        NSInteger len = [inputStream read:sub maxLength:1024];
        if (len > 0 && inputStream.streamError == nil) {
            [body appendBytes:(void *)sub length:len];
        }else{
            break;
        }
    }
    //发送请求的body
    NSDictionary *requestBody = [DoraemonUrlUtil convertDicFromData:body];


    NSString  *query = [request.URL query];
    
    DoraemMultiItem  *item = [DoraemMultiItem new];
    item.pId = [DoraemonManager shareInstance].pId;
    item.caseId = [DoraemMultiMockManger sharedInstance].caseId;
    item.contentType = [NSString stringWithFormat:@"%@",[request.allHTTPHeaderFields objectForKey:@"Content-Type"]];
    item.method = request.HTTPMethod;
    item.path = [request.URL path];
    item.fragment = [request.URL fragment];
    item.responseBody = responseBody;
    item.requestBody = [self excludeRequest:requestBody];
    item.query = [self excludeQuery:query];
//    NSDictionary *requestDict = [self excludeQuery:item.requestBody];
    NSLog(@"allHTTPHeaderFields == %@",request.allHTTPHeaderFields);
    
    item.originKey = [NSString stringWithFormat:@"method=%@&path=%@&fragment=%@&query=%@&contentType=%@&requestBody=%@ ",request.HTTPMethod,request.URL.path,item.fragment,item.query,item.contentType,item.requestBody];
    item.key = [self encodMd5:item.originKey];
    
    return item;
}

/*
 *
 */
- (NSString *)getKeyWithDoraemMultiItem:(DoraemMultiItem *)item  {

    item.originKey = [NSString stringWithFormat:@"method=%@&path=%@&fragment=%@&query=%@&contentType=%@&requestBody=%@ ",item.method,item.path,item.fragment,item.query,item.contentType,item.requestBody];
    item.key = [self encodMd5:item.originKey];
    
    return item.key;
    
    
}

/*
 * 在 request 里面去除  exclude
 */
- (NSDictionary *)excludeRequest:(NSDictionary *)request {
    
    NSMutableDictionary * newRequest = [NSMutableDictionary new];
    
    for(NSString *key in request){
        NSString *vaule = [request objectForKey:key];
        if (![[DoraemMultiMockManger sharedInstance].excludeArray containsObject:key] ) {
            [newRequest setValue:vaule forKey:key];
        }
    }
    return  newRequest;
    
    
}



/*
 * 在 query 里面去除  exclude
 */
- (NSDictionary *)excludeQuery:(NSString *)query {
    NSMutableDictionary * queryDict = [NSMutableDictionary new];
    
    NSArray *queryArray = [query componentsSeparatedByString:@"&"];
    for (NSString *queryItem in queryArray) {
        
        NSArray*array =  [queryItem componentsSeparatedByString:@"="];
        NSString *key = [array firstObject];
        NSString *vaule = [array lastObject];
        if (![[DoraemMultiMockManger sharedInstance].excludeArray containsObject:key] ) {
            [queryDict setValue:vaule forKey:key];
        }
        
    }
    return queryDict;
}


/*
 *  Md5 计算
 */
- (NSString *)encodMd5:(NSString *)input {
    
    const char *cStr = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cStr, (CC_LONG)strlen(cStr), digest);
    NSMutableString *output =  [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH *2];
    for (int i = 0; i <CC_MD5_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x",digest[i]];
    }

    return output;
}


@end
