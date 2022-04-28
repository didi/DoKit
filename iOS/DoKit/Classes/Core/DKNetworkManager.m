//
//  DKNetworkManager.m
//  DoraemonKit
//
//  Created by 唐佳诚 on 2022/4/28.
//

#import "DKNetworkManager.h"
#import <SocketRocket/SRWebSocket.h>

NS_ASSUME_NONNULL_BEGIN

@interface DKNetworkManager () <SRWebSocketDelegate>

@property(nonatomic, nullable, weak) SRWebSocket *webSocket;

@property(nonatomic, assign) int requestId;

@property(nonatomic, nullable, copy) NSUUID *sessionUUID;

@end

NS_ASSUME_NONNULL_END

@implementation DKNetworkManager

- (BOOL)isRunning {
    return self.webSocket.readyState == SR_OPEN;
}

- (void)startWebSocketWithUrl:(NSURL *)url {
    if (self.isRunning) {
#ifndef NS_BLOCK_ASSERTIONS
        NSAssert(NO, @"WebSocket session is already running.");
#endif

        return;
    }
    SRWebSocket *webSocket = [[SRWebSocket alloc] initWithURL:url];
    self.webSocket = webSocket;
    webSocket.delegate = self;
    [webSocket open];
    self.requestId = 0;
    
//    [webSocket sendString:<#(NSString *)string#> error:nil];
}

- (void)closeWebSocket {
#ifndef NS_BLOCK_ASSERTIONS
    NSAssert(self.isRunning, @"Cannot call -[DKNetworkManager closeWebSocket] before -[DKNetworkManager startWebSocketWithUrl:].");
#endif
    [self.webSocket close];
}

#pragma mark - SRWebSocketDelegate

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessageWithString:(NSString *)string {

}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {

}

- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {

}

@end
