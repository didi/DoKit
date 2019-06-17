//
//  DoraemonURLSessionDemux.h
//  AFNetworking
//
//  Created by yixiang on 2019/1/16.
//

#import <Foundation/Foundation.h>

@interface DoraemonURLSessionDemux : NSObject

- (instancetype)initWithConfiguration:(NSURLSessionConfiguration *)configuration;

@property (atomic, copy,   readonly ) NSURLSessionConfiguration *   configuration;

@property (atomic, strong, readonly ) NSURLSession *                session;

- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request delegate:(id<NSURLSessionDataDelegate>)delegate modes:(NSArray *)modes;

@end

