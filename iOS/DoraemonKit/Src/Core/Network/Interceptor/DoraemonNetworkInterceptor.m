//
//  DoraemonNetworkInterceptor.m
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import "DoraemonNetworkInterceptor.h"
#import "DoraemonNSURLProtocol.h"
#import "DoraemonNetFlowManager.h"
#import "DoraemonLargeImageDetectionManager.h"
#import "DoraemonNetFlowDataSource.h"
#import "DoraemonNetFlowHttpModel.h"
#import "DoraemonResponseImageModel.h"
#import "DoraemonDefine.h"
#import<CommonCrypto/CommonDigest.h>
static DoraemonNetworkInterceptor *instance = nil;

@interface DoraemonNetworkInterceptor()

@property (nonatomic, strong) NSHashTable *delegates;
@end

@implementation DoraemonNetworkInterceptor

- (NSHashTable *)delegates {
    if (_delegates == nil) {
        self.delegates = [NSHashTable weakObjectsHashTable];
    }
    return _delegates;
}

- (void)addDelegate:(id<DoraemonNetworkInterceptorDelegate>) delegate {
    [self.delegates addObject:delegate];
    [self updateURLProtocolInterceptStatus];
}

- (void)removeDelegate:(id<DoraemonNetworkInterceptorDelegate>)delegate {
    [self.delegates removeObject:delegate];
    [self updateURLProtocolInterceptStatus];
}
    

+ (instancetype)shareInstance {
    static dispatch_once_t once;
    dispatch_once(&once, ^{
        instance = [[DoraemonNetworkInterceptor alloc] init];
    });
    return instance;
}

- (BOOL)shouldIntercept {
    // 当有对象监听 拦截后的网络请求时，才需要拦截
    BOOL shouldIntercept = NO;
    
    for (id<DoraemonNetworkInterceptorDelegate> delegate in self.delegates) {
        if (delegate.shouldIntercept) {
            shouldIntercept = YES;
//            if (shouldIntercept) {
//                DoKitLog(@"yixiang shouldIntercept from %@",[delegate class]);
//            }
        }
    }
    return shouldIntercept;
}


- (void)updateURLProtocolInterceptStatus {
    if (self.shouldIntercept) {
        [NSURLProtocol registerClass:[DoraemonNSURLProtocol class]];
    }else{
        [NSURLProtocol unregisterClass:[DoraemonNSURLProtocol class]];
    }
}

- (void)updateInterceptStatusForSessionConfiguration: (NSURLSessionConfiguration *)sessionConfiguration {
    //BOOL shouldIntercept = [self shouldIntercept];
    if ([sessionConfiguration respondsToSelector:@selector(protocolClasses)]
        && [sessionConfiguration respondsToSelector:@selector(setProtocolClasses:)]) {
        NSMutableArray * urlProtocolClasses = [NSMutableArray arrayWithArray: sessionConfiguration.protocolClasses];
        Class protoCls = DoraemonNSURLProtocol.class;
        if ( ![urlProtocolClasses containsObject: protoCls]) {
            [urlProtocolClasses insertObject: protoCls atIndex: 0];
        } else if ([urlProtocolClasses containsObject: protoCls]) {
            [urlProtocolClasses removeObject: protoCls];
        }
        sessionConfiguration.protocolClasses = urlProtocolClasses;
    }
}

- (void)handleResultWithData: (NSData *)data
                    response: (NSURLResponse *)response
                     request: (NSURLRequest *)request
                       error: (NSError *)error
                   startTime: (NSTimeInterval)startTime {
    dispatch_async(dispatch_get_main_queue(), ^{
        for (id<DoraemonNetworkInterceptorDelegate> delegate in self.delegates) {
            [delegate doraemonNetworkInterceptorDidReceiveData:data response:response request:request error:error startTime:startTime];
        }
    });
}




-(NSData *)encodeMD5:(NSData *)input{
    
    unsigned char result[CC_MD5_DIGEST_LENGTH];
    CC_MD5(input.bytes, (CC_LONG)input.length, result);
    
    NSData *data =[[NSData alloc] initWithBytes:result length:CC_MD5_DIGEST_LENGTH];
    return data;
}

- (NSString *)md5:(NSString *)input {
    const char *cStr = [input UTF8String];
    unsigned char digest[CC_MD5_DIGEST_LENGTH];
    CC_MD5(cStr, (CC_LONG)strlen(cStr), digest);
    NSMutableString *output =  [NSMutableString stringWithCapacity:CC_MD5_DIGEST_LENGTH *2];
//    NSMutableString* ret = [NSMutableString stringWithCapacity: CC_MD5_DIGEST_LENGTH];
    for (int i = 0; i < CC_MD5_DIGEST_LENGTH; i++) {
        [output appendFormat:@"%02x",digest[i]];
    }

    return output;
}

- (NSString *)hexStringFromString:(NSString *)string{
   NSData *myD = [string dataUsingEncoding:NSUTF8StringEncoding];
   Byte *bytes = (Byte *)[myD bytes];
   //下面是Byte 转换为16进制。
   NSString *hexStr=@"";
   for(int i=0;i<[myD length];i++)
   {
       NSString *newHexStr = [NSString stringWithFormat:@"%x",bytes[i]&0xff];///16进制数
       if([newHexStr length]==1)
           hexStr = [NSString stringWithFormat:@"%@0%@",hexStr,newHexStr];
       else
           hexStr = [NSString stringWithFormat:@"%@%@",hexStr,newHexStr];
   }
   return hexStr;
}

//data转换为十六进制的string
- (NSString *)hexStringFromData:(NSData *)myD{
    
    Byte *bytes = (Byte *)[myD bytes];
    //下面是Byte 转换为16进制。
    NSString *hexStr=@"";
    for(int i=0;i<[myD length];i++)
        
    {
        NSString *newHexStr = [NSString stringWithFormat:@"%x",bytes[i]&0xff];///16进制数
        
        if([newHexStr length]==1)
            
            hexStr = [NSString stringWithFormat:@"%@0%@",hexStr,newHexStr];
        
        else
            
            hexStr = [NSString stringWithFormat:@"%@%@",hexStr,newHexStr];
    }
    NSLog(@"hex = %@",hexStr);
    
    return hexStr;
}

- (void)structureKeyByString:(NSString *)original {
  original  = @"method=POST&path=/gateway&fragment=null&query={\"api\":\"lj.u.d.changeOnline\",\"appKey\":\"b4f945fe780140d8a0d19d1f2d021db7\"}&contentType=application/json; charset=utf-8&requestBody={\"type\":1.0}";
    
   original = @"method=POST&path=/golden/stat&fragment=null&query={}&contentType=application/json;charset=utf-8&requestBody={\"attrs\":\"{\\\"module_id\\\":1602,\\\"static_version\\\":\\\"0.0.81\\\",\\\"module_version\\\":\\\"1.0.39\\\",\\\"app_id\\\":\\\"788119\\\",\\\"native_version\\\":\\\"6.8.2\\\",\\\"status\\\":0}\",\"e\":\"tech_mait_sdk_load\",\"ot\":\"android\",\"pn\":\"mait_tracker\",\"ua\":\"00000000-04a2-029e-ffff-ffffef05ac4a\",\"url\":\"hummer://user/dj_full_screen_page\"}";

  NSString *replaceString = [original stringByReplacingOccurrencesOfString:@"\\"withString:@""];
    
  NSData *aData = [replaceString dataUsingEncoding: NSUTF8StringEncoding];
  Byte *testByte = (Byte *)[aData bytes];
  NSData *data = [NSData dataWithBytes:testByte length:sizeof(testByte)];
  NSData *md5data = [self encodeMD5:data];
  NSString *keyString = [self hexStringFromData:md5data];

//  NSString *encodedUrl = [replaceString stringByAddingPercentEncodingWithAllowedCharacters:[NSCharacterSet URLQueryAllowedCharacterSet]];
  NSString *md5String = [self md5:original];
  keyString = [self hexStringFromString:md5String];
    
  NSLog(@"keyString === %@",keyString);
    
 
}

@end
