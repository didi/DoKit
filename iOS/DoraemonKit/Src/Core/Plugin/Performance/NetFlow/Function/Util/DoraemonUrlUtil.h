//
//  DoraemonUrlUtil.h
//  DoraemonKit
//
//  Created by yixiang on 2018/4/23.
//

#import <Foundation/Foundation.h>

@interface DoraemonUrlUtil : NSObject

+ (NSString *)convertJsonFromData:(NSData *)data;

+ (NSDictionary *)convertDicFromData:(NSData *)data;

+ (NSUInteger)getHeadersLengthWithRequest:(NSURLRequest *)request;

+ (void)requestLength:(NSURLRequest *)request callBack:(void (^)(NSUInteger))callBack;

+ (NSUInteger)getHeadersLength:(NSDictionary *)headers ;

+ (NSDictionary<NSString *, NSString *> *)getCookies:(NSURLRequest *)request ;

+ (int64_t)getResponseLength:(NSHTTPURLResponse *)response data:(NSData *)responseData;

@end
