//
//  NSURLProtocol+DoraemonWebKitSupport.h
//  AFNetworking
//
//  Created by yixiang on 2018/12/26.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface NSURLProtocol (DoraemonWebKitSupport)

+ (void)doraemon_wk_registerScheme:(NSString*)scheme;

+ (void)doraemon_wk_unregisterScheme:(NSString*)scheme;

@end

NS_ASSUME_NONNULL_END
