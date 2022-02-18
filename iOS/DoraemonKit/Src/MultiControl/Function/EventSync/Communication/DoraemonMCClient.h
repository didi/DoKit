//
//  DoraemonMCClient.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN


FOUNDATION_EXPORT NSNotificationName DoraemonMCClientStatusChanged;

@interface DoraemonMCClient : NSObject

+ (BOOL)isConnected;

+ (void)connectWithUrl:(NSString *)url;

+ (void)disConnect;

+ (void)showToast:(NSString *)toastContent;
@end

NS_ASSUME_NONNULL_END
