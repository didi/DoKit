//
//  DKNetworkManager.h
//  DoraemonKit
//
//  Created by 唐佳诚 on 2022/4/28.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKNetworkManager : NSObject

@property(readonly) BOOL isRunning;

- (void)startWebSocketWithUrl:(NSURL *)url;

- (void)closeWebSocket;

@end

NS_ASSUME_NONNULL_END
