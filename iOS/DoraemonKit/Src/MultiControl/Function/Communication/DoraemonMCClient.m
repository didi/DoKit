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
#import "DoraemonHomeWindow.h"
#import "DoraemonManager.h"
#import "UIColor+Doraemon.h"

@interface DoraemonMCClient () <SRWebSocketDelegate>

@property (strong, nonatomic) SRWebSocket *wsInstance;

@property (assign, nonatomic) BOOL isConnected;

@property (copy ,  nonatomic) void(^completion)(BOOL);

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

+ (void)connectWithUrl:(NSString *)url completion:(nonnull void (^)(BOOL))completion{
    [[self shareInstance] connectWithUrl:url completion:completion];
}

+ (void)disConnect {
    [[self shareInstance] disConnect];
}


- (void)disConnect {
    [self.wsInstance close];
    self.wsInstance = nil;
    self.isConnected = NO;
}

- (void)connectWithUrl:(NSString *)url completion:(nonnull void (^)(BOOL))completion {
    self.completion = completion;
    [self disConnect];
    NSURL *URL = [NSURL URLWithString:url];
    self.wsInstance = [[SRWebSocket alloc] initWithURLRequest:[NSURLRequest requestWithURL:URL]];
    self.wsInstance.delegate = self;
    [self.wsInstance open];
}



- (void)webSocketDidOpen:(SRWebSocket *)webSocket {
    self.isConnected = YES;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self showToast:@"连接成功"];
        [[DoraemonManager shareInstance] configEntryBtnBlingWithText:@"从" backColor:[UIColor doraemon_blue]];
    });
    if (self.completion) {
        self.completion(YES);
        self.completion = nil;
    }
}

- (void)webSocket:(SRWebSocket *)webSocket didReceiveMessage:(id)message {
    [DoraemonMCCommandExcutor excuteMessageStrFromNet:message];
}

- (void)webSocket:(SRWebSocket *)webSocket didFailWithError:(NSError *)error {
    self.isConnected = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self showToast:error.localizedDescription];
        [[DoraemonManager shareInstance] configEntryBtnBlingWithText:nil backColor:nil];
    });
    if (self.completion) {
        self.completion(NO);
        self.completion = nil;
    }
}
- (void)webSocket:(SRWebSocket *)webSocket didCloseWithCode:(NSInteger)code reason:(NSString *)reason wasClean:(BOOL)wasClean {
    self.isConnected = NO;
    dispatch_async(dispatch_get_main_queue(), ^{
        [self showToast:@"一机多控连接关闭"];
        [[DoraemonManager shareInstance] configEntryBtnBlingWithText:nil backColor:nil];
    });
    if (self.completion) {
        self.completion(NO);
        self.completion = nil;
    }
}


+ (void)showToast:(NSString *)toastContent {
    [[self shareInstance] showToast:toastContent];
}
- (void)showToast:(NSString *)toastContent {
    if (![toastContent isKindOfClass:[NSString class]] ||
        toastContent.length == 0) {
        return;
    }
    UIWindow *currentWindow = nil;
    if ([DoraemonHomeWindow shareInstance].hidden) {
        currentWindow = [UIApplication sharedApplication].keyWindow;
    }else {
        currentWindow = [DoraemonHomeWindow shareInstance];
    }
    if ([NSThread currentThread].isMainThread) {
        [DoraemonToastUtil showToastBlack:toastContent inView:currentWindow];
    }else {
        dispatch_async(dispatch_get_main_queue(), ^{
            [DoraemonToastUtil showToastBlack:toastContent inView:currentWindow];
        });
    }
}

@end
