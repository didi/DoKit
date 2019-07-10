//
//  DoraemonNetworkInterceptor.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@protocol DoraemonNetworkInterceptorDelegate <NSObject>
- (BOOL)shouldIntercept;

- (void)doraemonNetworkInterceptorDidReceiveData: (NSData *)data
                                              response: (NSURLResponse *)response
                                              request: (NSURLRequest *)request
                                              error: (NSError *)error
                                              startTime: (NSTimeInterval)startTime;
@end

@interface DoraemonNetworkInterceptor : NSObject
@property (nonatomic, assign) BOOL shouldIntercept;

+ (instancetype)shareInstance;

- (void)addDelegate:(id<DoraemonNetworkInterceptorDelegate>) delegate;
- (void)removeDelegate:(id<DoraemonNetworkInterceptorDelegate>)delegate;
- (void)updateInterceptStatusForSessionConfiguration: (NSURLSessionConfiguration *)sessionConfiguration;
- (void)handleResultWithData: (NSData *)data
                    response: (NSURLResponse *)response
                     request: (NSURLRequest *)request
                       error: (NSError *)error
                   startTime: (NSTimeInterval)startTime;
@end

NS_ASSUME_NONNULL_END
