//
//  DoraemMultiMockManger.m
//  DoraemonKit
//
//  Created by wzp on 2021/10/1.
//

#import "DoraemMultiMockManger.h"
#import "DoraemonMultiNetworkInterceptor.h"
// 单例

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
    
    NSLog(@"doraemonNetworkInterceptorDidReceiveData");
}

@end
