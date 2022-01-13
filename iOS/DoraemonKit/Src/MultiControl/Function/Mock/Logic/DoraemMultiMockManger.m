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
// 单例



@implementation DoraemMultiItem



@end

@interface DoraemMultiMockManger() 

@property (nonatomic, strong)NSMutableArray * uploadApiArray;

@end


@implementation DoraemMultiMockManger

+ (DoraemMultiMockManger *)sharedInstance {
    static dispatch_once_t once;
    static DoraemMultiMockManger *instance;
    dispatch_once(&once, ^{
        instance = [[DoraemMultiMockManger alloc] init];
        instance.uploadApiArray = [NSMutableArray new];
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
    
    
    if([request.URL.host isEqualToString:@"www.dokit.cn"]) {
        return;
    }
    
    
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
    NSString *requestBody = [[NSString alloc]initWithData:body encoding:NSUTF8StringEncoding];
    
    /*
     val k = "method=method&path=
     method&path=
     path&fragment=fragment&query=
     fragment&query=
     strQuery&contentType=requestContentType&requestBody=
     requestContentType&requestBody=
     strRequestBody"
     */
    NSString *key = [NSString stringWithFormat:@"method=%@&path=%@&requestBody=%@ ",request.HTTPMethod,request.URL.path,requestBody];
    NSString  *query = [request.URL query];
    
    DoraemMultiItem  *item = [DoraemMultiItem new];
    item.pId = [DoraemonManager shareInstance].pId;
    item.caseId = [DoraemMultiMockManger sharedInstance].caseId;
    item.contentType = [NSString stringWithFormat:@"%@;%@",response.MIMEType,response.textEncodingName];
    item.method = request.HTTPMethod;
    item.path = [request.URL path];
    item.requestBody = requestBody;
    item.responseBody = responseBody;
    item.key = [self encodMd5:key];
    item.query = [self excludeQuery:query];
    [[DoraemMultiMockManger sharedInstance].uploadApiArray addObject:item];
    
    [DoraemonMultiNetWorkSerivce uploadApiInfoWithItem:item sus:^(id  _Nonnull responseObject) {
        
    } fail:^(NSError * _Nonnull error) {
        
    }];

    
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
