//
//  DoraemonUrlUtil.h
//  Aspects
//
//  Created by yixiang on 2018/4/23.
//

#import <Foundation/Foundation.h>

@interface DoraemonUrlUtil : NSObject

+ (NSString *)convertJsonFromData:(NSData *)data;

+ (NSUInteger)getRequestLength:(NSURLRequest *)request;

+ (NSUInteger)getHeadersLength:(NSDictionary *)headers ;

+ (NSDictionary<NSString *, NSString *> *)getCookies:(NSURLRequest *)request ;

+ (int64_t)getResponseLength:(NSHTTPURLResponse *)response data:(NSData *)responseData;

+ (NSData *)getHttpBodyFromRequest:(NSURLRequest *)request;

@end
