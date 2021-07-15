//
//  DoraemonMCClient.m
//  DoraemonKit
//
//  Created by litianhao on 2021/6/30.
//

#import "DoraemonMCClient.h"
#import <SocketRocket/SocketRocket.h>
#import "DoraemonToastUtil.h"
#import "DoraemonMCCommandExcutor.h"

@interface DoraemonMCClient () <SRWebSocketDelegate>

@property (strong, nonatomic) SRWebSocket *wsInstance;

@property (assign, nonatomic) BOOL isConnected;

@end

@implementation DoraemonMCClient

+ (instancetype)shareInstance {
   static  DoraemonMCClient *instance = nil;
    static dispatch_once_t onceToken;
    dispatch_once(&onceToken, ^{
        instance = [[self alloc] init];
    });
    return instance;
}

+ (BOOL)isConnected {
    return [[self shareInstance] isConnected] ;
}

+ (void)connectWithUrl:(NSString *)url{
    [[self shareInstance] connectWithUrl:url];
}

+ (void)disConnect {
    [[self shareInstance] disConnect];
}

- (BOOL)isConnected {
    return self.isConnected;
}

- (void)disConnect {
    [self.wsInstance close];
    self.wsInstance = nil;
}

- (void)connectWithUrl:(NSString *)url {
    [self disConnect];
    
    NSURL *URL = [NSURL URLWithString:url];
    self.wsInstance = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:URL]];
    self.wsInstance.delegate = self;
    [self.wsInstance open];
}



- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    NSLog(@"didOpen");
    self.isConnected = YES;
    [self showToast:@"连接成功"];
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message {
    [DoraemonMCCommandExcutor excuteMessageStrFromNet:message];
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    self.isConnected = NO;
    [self showToast:error.localizedDescription];
}
- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    self.isConnected = NO;
    [self showToast:@"一机多控连接关闭"];
}

- (void)showToast:(NSString *)toastContent {
    if (![toastContent isKindOfClass:[NSString class]] ||
        toastContent.length == 0) {
        return;
    }
    if ([NSThread currentThread].isMainThread) {
        [DoraemonToastUtil showToastBlack:toastContent inView:[UIApplication sharedApplication].keyWindow];
    }else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [DoraemonToastUtil showToastBlack:toastContent inView:[UIApplication sharedApplication].keyWindow];
        });
    }
}

@end
