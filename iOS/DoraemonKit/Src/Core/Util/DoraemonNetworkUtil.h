//
//  DoraemonNetworkUtil.h
//  AFNetworking
//
//  Created by yixiang on 2019/2/28.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^DoraemonNetworkSucceedCallback)(NSDictionary *result);
typedef void (^DoraemonNetworkFailureCallback)(NSError *error);

#define DoraemonNetworkUtilUrl

@interface DoraemonNetworkUtil : NSObject
// get 请求
+ (void)getWithUrlString:(NSString *)url params:(NSDictionary *)params success:(DoraemonNetworkSucceedCallback)successAction
error:(DoraemonNetworkFailureCallback)errorAction;

// post 请求
+ (void)postWithUrlString:(NSString *)url params:(NSDictionary *)params success:(DoraemonNetworkSucceedCallback)successAction
error:(DoraemonNetworkFailureCallback)errorAction;

// patch请求
+ (void)patchWithUrlString:(NSString *)url params:(NSDictionary *)params success:(DoraemonNetworkSucceedCallback)successAction error:(DoraemonNetworkFailureCallback)errorAction;


@end

NS_ASSUME_NONNULL_END
