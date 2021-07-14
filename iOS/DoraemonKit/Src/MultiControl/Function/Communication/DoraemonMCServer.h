//
//  DoraemonMCServer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <Foundation/Foundation.h>

FOUNDATION_EXPORT NSInteger const kDoraemonMCServerPort;

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCServer : NSObject

+ (BOOL)startServerWithError:(NSError **)error;

+ (void)sendMessage:(NSString *)message;

+ (BOOL)isServer;

@end

NS_ASSUME_NONNULL_END
