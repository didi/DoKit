//
//  DoraemonMCServer.h
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DoraemonMCServer : NSObject

+ (void)sendMessage:(NSString *)message;

+ (BOOL)isOpen;

@end

NS_ASSUME_NONNULL_END
