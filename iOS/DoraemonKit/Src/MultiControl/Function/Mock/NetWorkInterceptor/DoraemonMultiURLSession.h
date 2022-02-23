//
//  DoraemonMultiURLSession.h
//  DoraemonKit
//
//  Created by wzp on 2021/9/23.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMultiURLSessionTaskInfo:NSObject

@property (atomic, strong, readonly) NSURLRequest  *request;

@end

@interface DoraemonMultiURLSession : NSObject


@property (atomic, copy,   readonly) NSURLSessionConfiguration * configuration;
@property (atomic, strong, readonly) NSURLSession              * seesion;

- (instancetype)initWithConfiguration:(nullable NSURLSessionConfiguration *)configuration;

- (NSURLSessionDataTask *)dataTaskWithRequest:(NSURLRequest *)request
                                     delegate:(id <NSURLSessionDataDelegate>)delegate
                                        modes:(NSArray *)modes;

- (DoraemonMultiURLSessionTaskInfo *)taskInfoForTask:(NSURLSessionTask *)task;

@end

NS_ASSUME_NONNULL_END
