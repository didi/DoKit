//
//  DoraemonUrlUtil.m
//  DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import "DoraemonUrlUtil.h"
#import "DoraemonNetFlowManager.h"

@implementation DoraemonUrlUtil

+ (NSString *)convertJsonFromData:(NSData *)data{
    if (!data) {
        return nil;
    }
    NSString *jsonString = nil;
    
    id jsonObject = [NSJSONSerialization JSONObjectWithData:data options:0 error:NULL];
    if ([NSJSONSerialization isValidJSONObject:jsonObject]) {
        jsonString = [[NSString alloc] initWithData:[NSJSONSerialization dataWithJSONObject:jsonObject options:NSJSONWritingPrettyPrinted error:NULL] encoding:NSUTF8StringEncoding];
        // NSJSONSerialization escapes forward slashes. We want pretty json, so run through and unescape the slashes.
        jsonString = [jsonString stringByReplacingOccurrencesOfString:@"\\/" withString:@"/"];
    } else {
        jsonString = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
    }
    return jsonString;
}

+ (NSDictionary *)convertDicFromData:(NSData *)data{
    if (!data) {
        return nil;
    }
    NSDictionary *jsonObj = nil;
    
    id jsonObject = [NSJSONSerialization JSONObjectWithData:data options:0 error:NULL];
    if ([NSJSONSerialization isValidJSONObject:jsonObject]){
        jsonObj = jsonObject;
    }else{
        NSString *str = [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
        if (!str) return jsonObj;
        NSArray *componentsArray =  [str componentsSeparatedByString:@"&"];
        NSMutableDictionary *dic = @{}.mutableCopy;
        [componentsArray enumerateObjectsUsingBlock:^(NSString *_Nonnull obj, NSUInteger idx, BOOL * _Nonnull stop) {
            NSArray *keyValues =  [obj componentsSeparatedByString:@"="];
            if (keyValues.count == 2) {
                [dic setValue:keyValues.lastObject forKey:keyValues.firstObject];
            }
        }];
        if (dic.allKeys.count > 0) {
            jsonObj = dic.copy;
        }
    }
    return jsonObj;
}
+ (void)requestLength:(NSURLRequest *)request callBack:(void (^)(NSUInteger))callBack {
    NSUInteger headersLength = [self getHeadersLengthWithRequest:request];
    [[DoraemonNetFlowManager shareInstance] httpBodyFromRequest:request bodyCallBack:^(NSData *body) {
        NSUInteger bodyLength = [body length];
        callBack(headersLength + bodyLength);
    }];
}

+ (NSUInteger)getHeadersLengthWithRequest:(NSURLRequest *)request {
    NSDictionary<NSString *, NSString *> *headerFields = request.allHTTPHeaderFields;
    NSDictionary<NSString *, NSString *> *cookiesHeader = [self getCookies:request];
    if (cookiesHeader.count) {
        NSMutableDictionary *headerFieldsWithCookies = [NSMutableDictionary dictionaryWithDictionary:headerFields];
        [headerFieldsWithCookies addEntriesFromDictionary:cookiesHeader];
        headerFields = [headerFieldsWithCookies copy];
    }
    return [self getHeadersLength:headerFields];
}

+ (NSUInteger)getHeadersLength:(NSDictionary *)headers {
    NSUInteger headersLength = 0;
    if (headers) {
        NSData *data = [NSJSONSerialization dataWithJSONObject:headers
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:nil];
        headersLength = data.length;
    }
    
    return headersLength;
}

+ (NSDictionary<NSString *, NSString *> *)getCookies:(NSURLRequest *)request {
    NSDictionary<NSString *, NSString *> *cookiesHeader;
    NSHTTPCookieStorage *cookieStorage = [NSHTTPCookieStorage sharedHTTPCookieStorage];
    NSArray<NSHTTPCookie *> *cookies = [cookieStorage cookiesForURL:request.URL];
    if (cookies.count) {
        cookiesHeader = [NSHTTPCookie requestHeaderFieldsWithCookies:cookies];
    }
    return cookiesHeader;
}

+ (int64_t)getResponseLength:(NSHTTPURLResponse *)response data:(NSData *)responseData{
    int64_t responseLength = 0;
    if (response && [response isKindOfClass:[NSHTTPURLResponse class]]) {
        NSHTTPURLResponse *httpResponse = (NSHTTPURLResponse *)response;
        NSDictionary<NSString *, NSString *> *headerFields = httpResponse.allHeaderFields;
        NSUInteger headersLength = [self getHeadersLength:headerFields];
        
        int64_t contentLength = 0.;
        if(httpResponse.expectedContentLength != NSURLResponseUnknownLength){
            contentLength = httpResponse.expectedContentLength;
        }else{
            contentLength = responseData.length;
        }
        
        responseLength = headersLength + contentLength;
    }
    return responseLength;
}

@end
