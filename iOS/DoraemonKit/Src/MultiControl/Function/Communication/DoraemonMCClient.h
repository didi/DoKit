//
//  DoraemonMCClient.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCClient : NSObject

+ (BOOL)isConnected;

+ (void)connectWithUrl:(NSString *)url completion:(void(^)(BOOL))completion;

+ (void)disConnect;

@end

NS_ASSUME_NONNULL_END
