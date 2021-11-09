//
//  DoraemonMultiNetworkInterceptor.h
//  DoraemonKit
//
//  Created by 0xd-cc on 2019/5/15.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

// 一机多控 网络拦截器协议
@protocol DoraemonMultiNetworkInterceptorDelegate <NSObject>

- (BOOL)shouldIntercept;

- (void)doraemonNetworkInterceptorDidReceiveData:(NSData *)data
                                        response:(NSURLResponse *)response
                                         request:(NSURLRequest *)request
                                           error:(NSError *)error
                                       startTime:(NSTimeInterval)startTime;
@end

// 网络比较差的协议

@protocol DoraemonMultiNetworkWeakDelegate <NSObject>

typedef NS_ENUM(NSUInteger, DoraemonMultiWeakNetType) {
    #pragma mark - 弱网选项对应
    // 断网
    DoraemonMultiWeakNetwork_Break,
    // 超时
    DoraemonMultiWeakNetwork_OutTime,
    // 限网
    DoraemonMultiWeakNetwork_WeakSpeed,
    //延时
    DoraemonMultiWeakNetwork_Delay
};

- (NSInteger)weakNetSelecte;

- (NSUInteger)delayTime;

- (void)handleWeak:(NSData *)data isDown:(BOOL)is;

@end

/*
 *  一机多控 网络拦截器
 */
@interface DoraemonMultiNetworkInterceptor : NSObject

@property (nonatomic, assign) BOOL shouldIntercept;

@property (nonatomic, weak) id<DoraemonMultiNetworkWeakDelegate> weakDelegate;

+ (instancetype)shareInstance;

- (void)addDelegate:(id<DoraemonMultiNetworkInterceptorDelegate>) delegate;
- (void)removeDelegate:(id<DoraemonMultiNetworkInterceptorDelegate>)delegate;
- (void)updateInterceptStatusForSessionConfiguration: (NSURLSessionConfiguration *)sessionConfiguration;
- (void)handleResultWithData: (NSData *)data
                    response: (NSURLResponse *)response
                     request: (NSURLRequest *)request
                       error: (NSError *)error
                   startTime: (NSTimeInterval)startTime;
@end

NS_ASSUME_NONNULL_END
